# Makefile for OpenTofu/Terraform operations
.PHONY: help init plan apply destroy output clean

# Variables
TF_CMD = tofu
TF_VAR_FILE = terraform.tfvars

help: ## Show this help message
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

init: ## Initialize OpenTofu
	$(TF_CMD) init

validate: ## Validate configuration
	$(TF_CMD) validate

plan: ## Plan infrastructure changes
	$(TF_CMD) plan -var-file=$(TF_VAR_FILE)

apply: ## Apply infrastructure changes
	$(TF_CMD) apply -var-file=$(TF_VAR_FILE) -auto-approve

destroy: ## Destroy infrastructure
	$(TF_CMD) destroy -var-file=$(TF_VAR_FILE) -auto-approve

output: ## Show outputs
	$(TF_CMD) output

output-json: ## Show outputs in JSON format
	$(TF_CMD) output -json

refresh: ## Refresh state
	$(TF_CMD) refresh

clean: ## Clean OpenTofu files
	rm -rf .terraform .terraform.lock.hcl terraform.tfstate*

# Deployment shortcuts
deploy: init validate plan apply output ## Full deployment (init -> plan -> apply -> output)

quick-apply: apply output ## Quick apply and show outputs

# Development helpers
fmt: ## Format OpenTofu files
	$(TF_CMD) fmt -recursive

check: fmt validate ## Format and validate 