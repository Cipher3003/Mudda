#!/usr/bin/fish

ssh -f -N -i ~/.ssh/postgresql-key.pem -L 5433:localhost:5432 ubuntu@52.91.233.153
