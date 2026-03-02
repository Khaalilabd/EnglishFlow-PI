#!/bin/bash

# Script pour mettre à jour l'URL remote après le renommage du dépôt

echo "🔄 Mise à jour de l'URL remote Git..."

# Mettez à jour l'URL remote (remplacez par le nouveau nom de votre dépôt)
git remote set-url origin https://github.com/Khaalilabd/Esprit-PIDEV-3A-2026-JungleInEnglish.git

# Vérifiez la nouvelle URL
echo "✅ Nouvelle URL remote:"
git remote -v

echo ""
echo "✅ Mise à jour terminée!"
echo "⚠️  Informez votre équipe de mettre à jour leur remote avec cette commande:"
echo "    git remote set-url origin https://github.com/Khaalilabd/Esprit-PIDEV-3A-2026-JungleInEnglish.git"
