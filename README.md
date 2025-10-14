## Build Containers

- ./gradlew :sender:jib - Build and push sender image
- ./gradlew :receiver:jib - Build and push receiver image
- ./gradlew :sender:jibDockerBuild - Build sender to local Docker
- ./gradlew :receiver:jibDockerBuild - Build receiver to local Docker