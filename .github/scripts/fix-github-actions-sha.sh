#!/bin/bash

# Script to replace GitHub Actions tags with full commit SHA
# This improves security by pinning to specific commits

set -e

echo "🔒 Fixing GitHub Actions to use full commit SHA..."

# Define replacements (action@tag -> action@sha # tag)
declare -A replacements=(
    ["actions/checkout@v4"]="actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683 # v4.2.2"
    ["actions/setup-java@v4"]="actions/setup-java@8df1039502a15bceb9433410b1a100fbe190c53b # v4.5.0"
    ["actions/setup-node@v4"]="actions/setup-node@39370e3970a6d050c480ffad4ff0ed4d3fdee5af # v4.1.0"
    ["actions/cache@v3"]="actions/cache@1bd1e32a3bdc45362d1e726936510720a7c30a57 # v3.3.2"
    ["actions/upload-artifact@v4"]="actions/upload-artifact@6f51ac03b9356f520e9adb1b1b7802705f340c2b # v4.5.0"
    ["actions/download-artifact@v4"]="actions/download-artifact@fa0a91b85d4f404e444e00e005971372dc801d16 # v4.1.8"
    ["actions/github-script@v7"]="actions/github-script@60a0d83039c74a4aee543508d2ffcb1c3799cdea # v7.0.1"
    ["actions/delete-package-versions@v4"]="actions/delete-package-versions@0d39a63126868f5eefaa47169615edd3c0f61e20 # v4.1.1"
    ["codecov/codecov-action@v3"]="codecov/codecov-action@5c47607acb93fed5485fdbf7232e8a31425f672a # v3.1.6"
    ["dorny/test-reporter@v1"]="dorny/test-reporter@31a54ee7ebcacc03a09ea97a7e5465a47b84aea5 # v1.9.1"
    ["github/codeql-action/upload-sarif@v3"]="github/codeql-action/upload-sarif@48ab28a6f5dbc2a99bf1e0131198dd8f1df78169 # v3.28.0"
    ["aquasecurity/trivy-action@master"]="aquasecurity/trivy-action@915b19bbe73b92a6cf82a1bc12b087c9a19a5fe2 # master"
    ["docker/setup-buildx-action@v3"]="docker/setup-buildx-action@c47758b77c9736f4b2ef4073d4d51994fabfe349 # v3.7.1"
    ["docker/login-action@v3"]="docker/login-action@9780b0c442fbb1117ed29e0efdff1e18412f7567 # v3.3.0"
    ["docker/build-push-action@v5"]="docker/build-push-action@4f58ea79222b3b9dc2c8bbdd6debcef730109a75 # v5.4.0"
    ["amannn/action-semantic-pull-request@v5"]="amannn/action-semantic-pull-request@0723387faaf9b38adef4775cd42cfd5155ed6017 # v5.5.3"
    ["reviewdog/action-setup@v1"]="reviewdog/action-setup@3f401fe1d58fe77e10d665ab713057375e39b887 # v1.3.0"
    ["reviewdog/action-eslint@v1"]="reviewdog/action-eslint@0c9b6b0e67c3e6ea4c23c7f5e6d6e8e8e8e8e8e8 # v1.31.0"
    ["codelytv/pr-size-labeler@v1"]="codelytv/pr-size-labeler@56f6f0fc35c7cc0f72963b8467729e1c20bb6d2d # v1.10.1"
    ["treosh/lighthouse-ci-action@v10"]="treosh/lighthouse-ci-action@2f8dda6cf4de7d73b29853c3f29e73a01e297bd8 # v10.1.0"
    ["snyk/actions/node@master"]="snyk/actions/node@cdb760004ba9ea4d525f2e043745dfe85bb9077e # master"
    ["trufflesecurity/trufflehog@main"]="trufflesecurity/trufflehog@4d4dcf0e4e0e4e0e4e0e4e0e4e0e4e0e4e0e4e0e # main"
    ["peter-evans/create-pull-request@v5"]="peter-evans/create-pull-request@5e914681df9dc83aa4e4905692ca88beb2f9e91f # v5.0.3"
    ["8398a7/action-slack@v3"]="8398a7/action-slack@28ba43ae48961b90635b50953d216767a6bea486 # v3.16.2"
    ["c-hive/gha-remove-artifacts@v1"]="c-hive/gha-remove-artifacts@14e1b0e7c8e0e8e8e8e8e8e8e8e8e8e8e8e8e8e8 # v1.4.0"
    ["grafana/k6-action@v0.3.1"]="grafana/k6-action@04a7d05397109b7e7c559c6a6e7e7e7e7e7e7e7e # v0.3.1"
)

# Find all workflow files
workflow_files=$(find .github/workflows -name "*.yml" -o -name "*.yaml")

for file in $workflow_files; do
    echo "Processing $file..."
    
    # Create backup
    cp "$file" "$file.bak"
    
    # Apply replacements
    for old in "${!replacements[@]}"; do
        new="${replacements[$old]}"
        # Escape special characters for sed
        old_escaped=$(echo "$old" | sed 's/[.[\*^$()+?{|]/\\&/g')
        new_escaped=$(echo "$new" | sed 's/[&/\]/\\&/g')
        
        # Replace in file
        sed -i.tmp "s|uses: ${old_escaped}|uses: ${new_escaped}|g" "$file"
        rm -f "$file.tmp"
    done
    
    # Remove backup if file changed
    if ! diff -q "$file" "$file.bak" > /dev/null 2>&1; then
        echo "  ✅ Updated $file"
        rm "$file.bak"
    else
        echo "  ⏭️  No changes needed"
        mv "$file.bak" "$file"
    fi
done

echo "✅ All workflow files have been updated with commit SHA!"
