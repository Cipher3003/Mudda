provider "aws" {
  region = "eu-north-1"
}

module "s3_bucket_media"{
    source = "../../module/s3"
    media_bucket_name=var.media_bucket_name 
}