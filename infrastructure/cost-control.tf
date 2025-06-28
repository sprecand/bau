# Cost Control and Monitoring

# SNS Topic for billing alerts
resource "aws_sns_topic" "billing_alerts" {
  name = "${var.project_name}-billing-alerts"

  tags = {
    Name = "${var.project_name}-billing-alerts"
  }
}

# SNS Subscription (you'll need to confirm via email)
resource "aws_sns_topic_subscription" "billing_alerts_email" {
  count     = var.alert_email != "" ? 1 : 0
  topic_arn = aws_sns_topic.billing_alerts.arn
  protocol  = "email"
  endpoint  = var.alert_email
}

# CloudWatch Billing Alarm
resource "aws_cloudwatch_metric_alarm" "billing_alarm" {
  alarm_name          = "${var.project_name}-billing-alarm"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "EstimatedCharges"
  namespace           = "AWS/Billing"
  period              = "86400" # 24 hours
  statistic           = "Maximum"
  threshold           = var.monthly_budget_limit
  alarm_description   = "This metric monitors aws billing charges"
  alarm_actions       = [aws_sns_topic.billing_alerts.arn]

  dimensions = {
    Currency = "USD"
  }

  tags = {
    Name = "${var.project_name}-billing-alarm"
  }
}

# Budget for monthly spending limit
resource "aws_budgets_budget" "monthly_budget" {
  name          = "${var.project_name}-monthly-budget"
  budget_type   = "COST"
  limit_amount  = var.monthly_budget_limit
  limit_unit    = "USD"
  time_unit     = "MONTHLY"
  time_period_start = "2024-01-01_00:00"

  notification {
    comparison_operator        = "GREATER_THAN"
    threshold                 = 80
    threshold_type            = "PERCENTAGE"
    notification_type         = "ACTUAL"
    subscriber_email_addresses = var.alert_email != "" ? [var.alert_email] : []
  }

  notification {
    comparison_operator        = "GREATER_THAN"
    threshold                 = 100
    threshold_type            = "PERCENTAGE"
    notification_type          = "FORECASTED"
    subscriber_email_addresses = var.alert_email != "" ? [var.alert_email] : []
  }

  tags = {
    Name = "${var.project_name}-monthly-budget"
  }
}

# Cost allocation tags
locals {
  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "OpenTofu"
    CostCenter  = "Development"
  }
}

# Lambda function for auto-shutdown (optional)
resource "aws_lambda_function" "auto_shutdown" {
  count = var.enable_auto_shutdown ? 1 : 0
  
  filename         = "auto_shutdown.zip"
  function_name    = "${var.project_name}-auto-shutdown"
  role            = aws_iam_role.lambda_shutdown_role[0].arn
  handler         = "index.handler"
  runtime         = "python3.9"
  timeout         = 60

  source_code_hash = data.archive_file.auto_shutdown_zip[0].output_base64sha256

  environment {
    variables = {
      CLUSTER_NAME = aws_ecs_cluster.main.name
    }
  }

  tags = local.common_tags
}

# Lambda code for auto-shutdown
data "archive_file" "auto_shutdown_zip" {
  count       = var.enable_auto_shutdown ? 1 : 0
  type        = "zip"
  output_path = "auto_shutdown.zip"
  
  source {
    content = <<EOF
import boto3
import os

def handler(event, context):
    ecs_client = boto3.client('ecs')
    cluster_name = os.environ['CLUSTER_NAME']
    
    # List all services in the cluster
    services = ecs_client.list_services(cluster=cluster_name)
    
    # Scale down all services to 0
    for service_arn in services['serviceArns']:
        ecs_client.update_service(
            cluster=cluster_name,
            service=service_arn,
            desiredCount=0
        )
    
    return {
        'statusCode': 200,
        'body': f'Scaled down all services in {cluster_name}'
    }
EOF
    filename = "index.py"
  }
}

# IAM role for Lambda auto-shutdown
resource "aws_iam_role" "lambda_shutdown_role" {
  count = var.enable_auto_shutdown ? 1 : 0
  name  = "${var.project_name}-lambda-shutdown-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })

  tags = local.common_tags
}

# IAM policy for Lambda to manage ECS
resource "aws_iam_role_policy" "lambda_shutdown_policy" {
  count = var.enable_auto_shutdown ? 1 : 0
  name  = "${var.project_name}-lambda-shutdown-policy"
  role  = aws_iam_role.lambda_shutdown_role[0].id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "arn:aws:logs:*:*:*"
      },
      {
        Effect = "Allow"
        Action = [
          "ecs:ListServices",
          "ecs:UpdateService",
          "ecs:DescribeServices"
        ]
        Resource = "*"
      }
    ]
  })
}

# EventBridge rule for auto-shutdown (daily at 10 PM UTC)
resource "aws_cloudwatch_event_rule" "auto_shutdown_schedule" {
  count               = var.enable_auto_shutdown ? 1 : 0
  name                = "${var.project_name}-auto-shutdown-schedule"
  description         = "Trigger auto-shutdown Lambda daily"
  schedule_expression = "cron(0 22 * * ? *)" # 10 PM UTC daily

  tags = local.common_tags
}

# EventBridge target
resource "aws_cloudwatch_event_target" "auto_shutdown_target" {
  count     = var.enable_auto_shutdown ? 1 : 0
  rule      = aws_cloudwatch_event_rule.auto_shutdown_schedule[0].name
  target_id = "AutoShutdownLambdaTarget"
  arn       = aws_lambda_function.auto_shutdown[0].arn
}

# Lambda permission for EventBridge
resource "aws_lambda_permission" "allow_eventbridge" {
  count         = var.enable_auto_shutdown ? 1 : 0
  statement_id  = "AllowExecutionFromEventBridge"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.auto_shutdown[0].function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.auto_shutdown_schedule[0].arn
} 