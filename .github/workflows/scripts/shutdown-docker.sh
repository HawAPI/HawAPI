#!/usr/bin/env bash

# The HawAPI tests uses TestContainers to initialize a real PostgreSQL database
# for 'repository' and 'integration tests with 'reuse' flag. According to the docs,
# after all tests the docker container will still' running:
#
# "Reusable Containers is still an experimental feature and the behavior can change.
# Those containers won't stop after all tests are finished."
# Ref: https://java.testcontainers.org/features/reuse/

stop_only=false
for arg in "$@"
do
    if [[ $arg == "--stop-only" ]]
    then
        stop_only=true
    fi
done

echo 'Running processes: '
docker ps -a --format '{{.ID}} ({{.Names}})'
echo

ids=$(docker ps -a -q)
for id in $ids
do
  if $stop_only; then
      echo "Stopping: $id"
      docker stop "$id"
      continue
  fi

  echo "Stopping and Deleting: $id"
  docker stop "$id" && docker rm "$id"
done