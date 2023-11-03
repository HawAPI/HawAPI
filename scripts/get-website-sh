#!/usr/bin/env bash

org_name="HawAPI"
website_repository="website"
docs_repository="docs"
website_zip="https://github.com/${org_name}/${website_repository}/archive/refs/heads/release.zip"
docs_zip="https://github.com/${org_name}/${docs_repository}/archive/refs/heads/release.zip"

green=$(tput setaf 2)
yellow=$(tput setaf 3)
blue=$(tput setaf 4)
cyan=$(tput setaf 6)

clean_before=false

# Check args

for arg in "$@"
do
    if [[ $arg == "--clean-before" ]]
    then
        clean_before=true
    fi

    if [[ $arg == "--help" ]]
    then
        echo "${blue}Usage: ${green}$0 [option...]" >&2
        echo
        echo "   ${cyan}-B, --clean-before           ${green}Remove '.hawapi/' directory before download the website"
        echo "   ${cyan}-H, --clean-hawapi           ${green}Remove '.hawapi/' directory"
        echo "   ${cyan}-D, --clean-downloads        ${green}Remove '.downloads/' directory"
        echo "   ${cyan}-S, --clean-static           ${green}Remove 'resources/static/' directory"
        echo "   ${cyan}-A, --clean-all              ${green}Remove all directories related with website build"
        echo
        exit 0
    fi
done

# Clean

## Remove '.hawapi/' before building website
if $clean_before; then
    echo "${cyan}[$0] ${green}Removing '.hawapi/' and 'src/main/resources/static/' folders..."
    rm -rf .hawapi/
    rm -rf src/main/resources/static/
fi

# Check location

if ! [ -d "src/" ]; then
    echo "${yellow}Unknown location! Cancelling"
    exit 1
fi

# Start script

echo
echo "${blue}Script: ${green}$0"
echo

# Check all requisites

## Check if '.hawapi/website' already exist. If not, create it.
if ! [ -d ".hawapi/website" ]; then
    echo "${cyan}[$0] ${green}Directory '.hawapi/website' not found!"

    ## Check if '.downloads/' already exist. If not, download the project from git repository.
    if ! [ -d ".downloads/" ]; then
        echo "${cyan}[$0] ${green}Directory '.downloads/' not found!"
        mkdir -p .downloads/
        wget ${website_zip} -O "./.downloads/${website_repository}-release.zip" -q --show-progress
        echo "${cyan}[$0] Downloading '${org_name}/${website_repository}' from Github..."
        wget ${docs_zip} -O "./.downloads/${docs_repository}-release.zip" -q --show-progress
        echo "${cyan}[$0] Downloading '${org_name}/${docs_repository}' from Github..."
    fi

    echo "${cyan}[$0] ${green}Extracting files into '.hawapi/website'..."
    mkdir -p .hawapi/website
    unzip -q .downloads/${website_repository}-release.zip -d .hawapi/website/
    unzip -q .downloads/${docs_repository}-release.zip -d .hawapi/docs/
    mv .hawapi/website/${website_repository}-release/* .hawapi/website/
    mkdir .hawapi/website/docs/
    mv .hawapi/docs/${docs_repository}-release/* .hawapi/website/docs/
    rm -rf .hawapi/website/${website_repository}-release/
    rm -rf .hawapi/docs/

    # Fix wrong 404 location. This will follow the spring boot error path requirement
    mv .hawapi/website/error/404/index.html .hawapi/website/error/404.html
    rm -rf .hawapi/website/error/404/
fi

# Copy website files into 'static' folder

if [ -d "./src/main/resources/static/" ]; then
    echo "${cyan}[$0] ${green}Found files inside 'resources/static/'! Deleting all..."
    rm -rf ./src/main/resources/static/*
fi

if ! [ -d "./src/main/resources/static/" ]; then
    mkdir -p ./src/main/resources/static/
fi

echo "${cyan}[$0] ${green}Moving files from '.hawapi/website/' to 'resources/static/'"
mv .hawapi/website/* ./src/main/resources/static/
rm -rf .hawapi/website

# Clean

./scripts/clean-website.sh "$@"