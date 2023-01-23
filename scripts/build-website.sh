#!/usr/bin/env bash

org_name="HawAPI"
repository_name="website"
repository="https://github.com/${org_name}/${repository_name}/archive/refs/heads/main.zip"

red=$(tput setaf 1)
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
        echo "   ${cyan}-B, --clean-before           ${green}Remove '.hawapi/' directory before building the website"
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
    echo "${cyan}[$0] ${green}Removing '.hawapi/' folder..."
    echo "$PWD"
    rm -rf .hawapi/
fi

# Check location

if ! [ -d "src/" ]; then
    cd ..
    echo
    echo "${yellow}Unknown location! Moving to: $PWD"

    if ! [ -d "src/" ]; then
        echo
        echo "${red}Unknown location! Exiting"
        exit 1
    fi
fi

# Start script

echo
echo "${blue}Script: ${green}$0"
echo
echo "${cyan}[$0] ${green}See all requisites: https://github.com/HawAPI/HawAPI/blob/main/GETTING-STARTED.md#prerequisites"
echo "${cyan}[$0] ${green}Checking prerequisites for building website..."
echo

## Check all requisites
if ! type npm; then
    echo "${cyan}[$0] ${green}<npm> command not found!"
    exit 1
else
    if ! type yarn; then
        ## Yarn is required. Ask to install (GLOBALLY)
        echo "${cyan}[$0] ${green}<Yarn> command not found!"
        echo "${cyan}[$0] ${green}Install yarn globally? (Y/n)"
        read -n1 -s -r yarn_response
        
        if ! echo "$yarn_response" | grep '^[Yy]\?$'; then
            echo 'No'
            exit 1
        fi

        echo "${cyan}[$0] ${green}Installing yarn..."
        npm install --global yarn
    fi
fi

if ! type retype; then
    ## Retype is required. Ask to install (LOCALLY)
    echo "${cyan}[$0] ${green}<Retype> command not found!"
    echo "${cyan}[$0] ${green}Install retype? (Y/n)"
    read -n1 -s -r retype_response
    
    if ! echo "$retype_response" | grep '^[Yy]\?$'; then
        echo 'No'
        exit 1
    fi

    echo "${cyan}[$0] ${green}Installing retype..."
    yarn global add retypeapp
fi

echo
echo "${cyan}[$0] ${green}All requisites found!"

## Check if '.hawapi/website' already exist. If not, create it.
if ! [ -d ".hawapi/website" ]; then
    echo "${cyan}[$0] ${green}Directory '.hawapi/website' not found!"

    ## Check if '.downloads/' already exist. If not, download the project from git repository.
    if ! [ -d ".downloads/" ]; then
        echo "${cyan}[$0] ${green}Directory '.downloads/' not found! Downloading '${org_name}/${repository_name}' from Github..."
        mkdir -p .downloads/
        wget ${repository} -q --show-progress -P .downloads/
    fi

    echo "${cyan}[$0] ${green}Extracting 'main.zip' into '.hawapi/website'..."
    mkdir -p .hawapi/website
    unzip -q .downloads/main.zip -d .hawapi/website/
    mv .hawapi/website/${repository_name}-main/* .hawapi/website/
    rm -rf .hawapi/website/${repository_name}-main/
fi

echo "${cyan}[$0] ${green}Building the website..."
cd .hawapi/website/ || exit 1

if ! [ -d "./node_modules" ]; then
    echo "${cyan}[$0] ${green}Directory './node_modules' not found! Running 'yarn'!"
    echo
    yarn
    echo
fi

echo
yarn build-all
echo

# Website and Docs adaptation

echo "${cyan}[$0] ${green}Starting website/docs adaptation..."
echo

echo "${cyan}[$0] ${green}Removing '.nojekyll' file"
rm -rf ./build/docs/.nojekyll

## Try to unzip and modify the 'sitemap.xml.gz' file.
if ! type gunzip; then
    ## If command 'gunzip' don't exist. Just remove the file.
    echo "${cyan}[$0] ${green}<gunzip> command not found! Removing 'sitemap.xml.gz' file"
    rm -rf ./build/docs/sitemap.xml.gz
else
    echo "${cyan}[$0] ${green}Unzipping 'sitemap.xml.gz' file"
    gunzip ./build/docs/sitemap.xml.gz
    echo "${cyan}[$0] ${green}Replacing '.id/' with '.id/docs/'"
    echo "${cyan}[$0] ${green}Moving 'sitemap.xml.gz' file to './build/sitemap-1.xml'"
    sed 's#.id/#.id/docs/#' ./build/docs/sitemap.xml > ./build/sitemap-1.xml

    echo "${cyan}[$0] ${green}Adding 'https://hawapi.theproject.id/sitemap-1.xml' to './build/docs/robots.txt'"
    echo 'Sitemap: https://hawapi.theproject.id/sitemap-1.xml' >> ./build/docs/robots.txt
fi

echo "${cyan}[$0] ${green}Moving 'robots.txt' to './build/robots.txt'"
sed 's/sitemap.xml.gz/sitemap-0.xml/' ./build/docs/robots.txt > ./build/robots.txt
rm -rf ./build/docs/robots.txt
rm -rf ./build/sitemap-index.xml

# Finalization

if [ -d "../../src/main/resources/static/" ]; then
    echo "${cyan}[$0] ${green}Found files inside 'resources/static/'! Deleting all..."
    rm -rf ../../src/main/resources/static/*
fi

if ! [ -d "../../src/main/resources/static/" ]; then
    mkdir -p ../../src/main/resources/static/
fi

echo "${cyan}[$0] ${green}Moving files from './build/' to 'resources/static/'"
mv ./build/* ../../src/main/resources/static/

# Clean

cd ../..
./scripts/clean-website.sh "$@"