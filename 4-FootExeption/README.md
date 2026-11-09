# Gestionnaire de match de football

L'application Java Swing et la base Oracle se lancent ensemble avec Docker. L'interface graphique est affichée dans le navigateur via noVNC.

## Prérequis

- Docker Desktop (Windows ou macOS) ou Docker Engine avec le plugin Docker Compose (Linux)
- Un navigateur web

## Configuration

Le projet utilise des variables d’environnement Docker. Pour un lancement local simple, copiez le fichier d’exemple :

```bash
cp .env.example .env
```

Vous pouvez ensuite modifier les mots de passe si besoin dans le fichier `.env`.

## Démarrage

Dans un terminal, placez-vous dans ce dossier puis exécutez :

```bash
docker compose up --build -d
```

Au premier lancement, Oracle initialise la base et ses données de départ. Cela peut prendre quelques minutes. Lorsque les conteneurs sont démarrés, ouvrez :

<http://localhost:6080/vnc.html?autoconnect=true&resize=remote>

## Arrêt et données

Pour arrêter l'application et Oracle sans supprimer les données :

```bash
docker compose down
```

Les données Oracle restent conservées dans le volume `oracle-data` et `Base.sql` ne s'exécute qu'à la première initialisation. Pour repartir de zéro, supprimez aussi le volume (cela efface les données enregistrées) :

```bash
docker compose down -v
```

L'interface noVNC est liée à `localhost` et n'est pas destinée à être exposée directement sur Internet.
