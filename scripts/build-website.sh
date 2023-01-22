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
clean_hawapi=false
clean_downloads=false
clean_static=false

# Check args
for arg in "$@"
do
    if [[ $arg == "--clean-hawapi" ]]
    then
        clean_hawapi=true
    fi

    if [[ $arg == "--clean-downloads" ]]
    then
        clean_downloads=true
    fi

    if [[ $arg == "--clean-static" ]]
    then
        clean_static=true
    fi

    if [[ $arg == "--clean-all" ]]
    then
        clean_hawapi=true
        clean_downloads=true
        clean_static=true
    fi

    if [[ $arg == "--clean-before" ]]
    then
        clean_before=true
    fi
done

# Clean

if $clean_before; then
    echo "${cyan}[$0] ${green}Removing '.hawapi/' folder..."
    echo "$PWD"
    rm -rf .hawapi/
fi

# Check location

if ! [ -d "src/" ]; then
    cd ..
    echo ''
    echo "${yellow}Unknown location! Moving to: $PWD"

    if ! [ -d "src/" ]; then
        echo ''
        echo "${red}Unknown location! Exiting"
        exit 1
    fi
fi

# Start script

echo ''
echo "${blue}Script: ${green}$0"
echo "${blue}Configuration:"
echo -e "  * ${cyan}Clean '.hawapi/' directory after setup: ${green}${clean_hawapi}"
echo -e "  * ${cyan}Clean '.downloads/' directory after setup: ${green}${clean_downloads}"
echo -e "  * ${cyan}Clean '[...]/resources/static' directory after setup: ${green}${clean_static}"
echo ''
echo "${cyan}[$0] ${green}See all requisites: https://github.com/HawAPI/HawAPI/blob/main/GETTING-STARTED.md#prerequisites"
echo "${cyan}[$0] ${green}Checking prerequisites for building website..."
echo ''

if ! type npm; then
    echo "${cyan}[$0] ${green}<npm> command not found!"
    exit 1
else
    if ! type yarn; then
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
    echo "${cyan}[$0] ${green}<Retype> command not found!"
    echo "${cyan}[$0] ${green}Install retype globally? (Y/n)"
    read -n1 -s -r retype_response
    
    if ! echo "$retype_response" | grep '^[Yy]\?$'; then
        echo 'No'
        exit 1
    fi

    echo "${cyan}[$0] ${green}Installing retype..."
    yarn global add retypeapp
fi

echo ''
echo "${cyan}[$0] ${green}All requisites found!"

if ! [ -d ".hawapi/website" ]; then
    echo "${cyan}[$0] ${green}Directory '.hawapi/website' not found!"

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
cd .hawapi/website/ || exit

if ! [ -d "./node_modules" ]; then
    echo "${cyan}[$0] ${green}Directory './node_modules' not found! Running 'yarn'!"
    echo ''
    yarn
    echo ''
fi

echo ''
yarn build-all
echo ''

# Website and Docs adaptation

echo "${cyan}[$0] ${green}Starting website/docs adaptation..."
echo ''

echo "${cyan}[$0] ${green}Removing '.nojekyll' file"
rm -rf ./build/docs/.nojekyll

if ! type gunzip; then
    echo "${cyan}[$0] ${green}<gunzip> command not found! Removing 'sitemap.xml.gz' file"
    rm -rf ./build/docs/sitemap.xml.gz
else
    echo "${cyan}[$0] ${green}Unzipping 'sitemap.xml.gz' file"
    gunzip ./build/docs/sitemap.xml.gz
    echo "${cyan}[$0] ${green}Replacing '.id/' with '.id/docs/'"
    echo "${cyan}[$0] ${green}Moving 'sitemap.xml.gz' file to './build/sitemap-1.xml'"
    sed 's#.id/#.id/docs/#' ./build/docs/sitemap.xml > ./build/sitemap-1.xml
fi

echo "${cyan}[$0] ${green}Adding 'https://hawapi.theproject.id/sitemap-1.xml' to './build/docs/robots.txt'"
echo 'Sitemap: https://hawapi.theproject.id/sitemap-1.xml' >> ./build/docs/robots.txt

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

if $clean_hawapi; then
    echo "${cyan}[$0] ${green}Removing '.hawapi/' folder..."
    rm -rf ../../.hawapi/
fi

if $clean_downloads; then
    echo "${cyan}[$0] ${green}Removing '.downloads/' folder..."
    rm -rf ../../.downloads/
fi

if $clean_static; then
    echo "${cyan}[$0] ${green}Removing 'resources/static/' folder..."
    rm -rf ../../src/main/resources/static/
fi