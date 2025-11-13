resource "aws_s3_bucket" "bucket_media_url" {
  bucket = var.media_bucket_name
}