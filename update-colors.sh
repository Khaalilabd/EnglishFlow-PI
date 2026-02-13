#!/bin/bash

# Script pour mettre à jour les couleurs de la landing page
# Palette Jungle in English

CSS_FILE="frontend/src/assets/css/templatemo-scholar.css"

echo "🎨 Mise à jour des couleurs vers la palette Jungle in English..."

# Remplacer #f1f0fe (violet clair) par #F7EDE2 (beige clair)
sed -i.bak 's/#f1f0fe/#F7EDE2/g' "$CSS_FILE"

# Remplacer #7a6ad8 (violet) par #2D5757 (vert foncé)
sed -i.bak 's/#7a6ad8/#2D5757/g' "$CSS_FILE"

# Supprimer le fichier backup
rm -f "${CSS_FILE}.bak"

echo "✅ Couleurs mises à jour!"
echo ""
echo "Palette appliquée:"
echo "  - #F7EDE2 (Beige clair - secondary)"
echo "  - #2D5757 (Vert foncé - primary)"
