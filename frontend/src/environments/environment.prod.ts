export const environment = {
  production: true,
  apiUrl: (window as any)['env']?.['apiUrl'] || 'https://api.bau-platform.com/api/v1',
  aws: {
    region: (window as any)['env']?.['awsRegion'] || 'eu-central-1',
    userPoolId: (window as any)['env']?.['awsUserPoolId'] || '',
    clientId: (window as any)['env']?.['awsClientId'] || ''
  }
}; 