# Guide de Format des Questions de Quiz

## Format des Options

### Pour les questions à choix multiples (MCQ)

Les options doivent être séparées par le caractère **pipe** `|` (barre verticale).

**Exemple:**
```
Options: Paris|London|Berlin|Madrid
Correct Answer: Paris
```

### Pour les questions Vrai/Faux (TRUE_FALSE)

Les options sont automatiquement générées. Vous pouvez laisser le champ vide ou mettre:
```
Options: True|False
Correct Answer: True
```
(ou `False` selon la bonne réponse)

### Pour les questions ouvertes (OPEN)

Pas besoin d'options. Mettez simplement la réponse correcte:
```
Options: (laisser vide)
Correct Answer: La réponse attendue
```

## Exemples Complets

### Question MCQ
- **Question:** Quelle est la capitale de la France?
- **Type:** MCQ
- **Options:** `Paris|London|Berlin|Madrid`
- **Correct Answer:** `Paris`
- **Points:** 10

### Question TRUE_FALSE
- **Question:** La Terre est ronde
- **Type:** TRUE_FALSE
- **Options:** `True|False` (ou laisser vide)
- **Correct Answer:** `True`
- **Points:** 5

### Question OPEN
- **Question:** Expliquez la photosynthèse
- **Type:** OPEN
- **Options:** (laisser vide)
- **Correct Answer:** Processus par lequel les plantes convertissent la lumière en énergie
- **Points:** 20

## Notes Importantes

- **NE PAS** utiliser de virgules pour séparer les options
- **UTILISER** le caractère pipe `|` uniquement
- La réponse correcte doit correspondre EXACTEMENT à l'une des options (sensible à la casse)
- Pour TRUE_FALSE, utilisez exactement `True` ou `False` (avec majuscule)
