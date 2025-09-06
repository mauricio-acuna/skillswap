export interface Config {
  API_BASE_URL: string;
  AGORA_APP_ID: string;
  GOOGLE_CALENDAR_CLIENT_ID: string;
  FCM_SENDER_ID: string;
  ENVIRONMENT: 'development' | 'staging' | 'production';
  WEBSOCKET_URL: string;
  DEEP_LINK_URL: string;
  API_TIMEOUT: number;
  CACHE_DURATION: number;
  MAX_FILE_SIZE: number;
  SUPPORTED_VIDEO_FORMATS: string[];
  SUPPORTED_IMAGE_FORMATS: string[];
}

const developmentConfig: Config = {
  API_BASE_URL: 'http://localhost:8080/api/v1',
  AGORA_APP_ID: 'dev_agora_app_id',
  GOOGLE_CALENDAR_CLIENT_ID: 'dev_google_client_id',
  FCM_SENDER_ID: 'dev_fcm_sender_id',
  ENVIRONMENT: 'development',
  WEBSOCKET_URL: 'ws://localhost:8080',
  DEEP_LINK_URL: 'skillswap://dev',
  API_TIMEOUT: 10000,
  CACHE_DURATION: 300000, // 5 minutes
  MAX_FILE_SIZE: 10485760, // 10MB
  SUPPORTED_VIDEO_FORMATS: ['mp4', 'avi', 'mov'],
  SUPPORTED_IMAGE_FORMATS: ['jpg', 'jpeg', 'png', 'webp']
};

const stagingConfig: Config = {
  API_BASE_URL: 'https://api-staging.skillswap.com/api/v1',
  AGORA_APP_ID: 'staging_agora_app_id',
  GOOGLE_CALENDAR_CLIENT_ID: 'staging_google_client_id',
  FCM_SENDER_ID: 'staging_fcm_sender_id',
  ENVIRONMENT: 'staging',
  WEBSOCKET_URL: 'wss://api-staging.skillswap.com',
  DEEP_LINK_URL: 'skillswap://staging',
  API_TIMEOUT: 10000,
  CACHE_DURATION: 600000, // 10 minutes
  MAX_FILE_SIZE: 10485760, // 10MB
  SUPPORTED_VIDEO_FORMATS: ['mp4', 'avi', 'mov'],
  SUPPORTED_IMAGE_FORMATS: ['jpg', 'jpeg', 'png', 'webp']
};

const productionConfig: Config = {
  API_BASE_URL: 'https://api.skillswap.com/api/v1',
  AGORA_APP_ID: 'prod_agora_app_id',
  GOOGLE_CALENDAR_CLIENT_ID: 'prod_google_client_id',
  FCM_SENDER_ID: 'prod_fcm_sender_id',
  ENVIRONMENT: 'production',
  WEBSOCKET_URL: 'wss://api.skillswap.com',
  DEEP_LINK_URL: 'skillswap://app',
  API_TIMEOUT: 15000,
  CACHE_DURATION: 1800000, // 30 minutes
  MAX_FILE_SIZE: 10485760, // 10MB
  SUPPORTED_VIDEO_FORMATS: ['mp4', 'avi', 'mov'],
  SUPPORTED_IMAGE_FORMATS: ['jpg', 'jpeg', 'png', 'webp']
};

const getConfig = (): Config => {
  if (__DEV__) {
    return developmentConfig;
  }
  
  // Here you could check for staging environment
  // For now, defaulting to production for non-dev builds
  return productionConfig;
};

export const config = getConfig();
