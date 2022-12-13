#!/bin/bash
build_info=$(git rev-parse --abbrev-ref HEAD)
commit_info=$(git rev-parse HEAD)
repo_url_info=$(git config --get remote.origin.url)

mvn clean package spring-boot:build-info install -Dbuild.git.commit="${commit_info}" -Dbuild.git.branch="${build_info}" -Dbuild.git.url="${repo_url_info}"