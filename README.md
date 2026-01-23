## Prerequisites
- installed podman
- installed podman-docker
- jdk 21
- node 22

## Build Containers

```bash
#jvm
./gradlew :sender:jibDockerBuild
./gradlew :receiver:jibDockerBuild
```

```bash
#graalvm
./gradlew :receiver:bootBuildImage --imageName=graalvm-performance/receiver-graalvm
./gradlew :sender:bootBuildImage --imageName=graalvm-performance/sender-graalvm
```

## Run pods

```bash
#First time only
podman network create demo-network
```

```bash
# jvm
podman play kube --network demo-network .k8s/receiver.yaml
podman play kube --network demo-network .k8s/sender.yaml
```

```bash
# graalvm
podman play kube --network demo-network .k8s/receiver-graalvm.yaml
podman play kube --network demo-network .k8s/sender-graalvm.yaml
```

## Run front end

```bash
npm run dev
# access at http://localhost:5173/
```

## Stop pods

```bash
#jvm
podman play kube --down .k8s/receiver.yaml
podman play kube --down .k8s/sender.yaml
```

```bash
#graalvm
podman play kube --down .k8s/receiver-graalvm.yaml
podman play kube --down .k8s/sender-graalvm.yaml
```

## Restart jvm pods

```bash
podman play kube --down .k8s/receiver.yaml
podman play kube --down .k8s/sender.yaml
podman play kube --network demo-network .k8s/receiver.yaml
podman play kube --network demo-network .k8s/sender.yaml
```

## Restart graalvm pods

```bash
podman play kube --down .k8s/receiver-graalvm.yaml
podman play kube --down .k8s/sender-graalvm.yaml
podman play kube --network demo-network .k8s/receiver-graalvm.yaml
podman play kube --network demo-network .k8s/sender-graalvm.yaml
```

## Check pods

```bash
podman pod ps
```

## Findings for my machine

### Build-time (incl OCI image):

JVM-sender: 8s
JVM-receiver: 10s

GraalVM-sender: 2m 3s
GraalVM-receiver: 31s

### Image size

JVM-sender: 328 MB
JVM-receiver: 328 MB

GraalVM-sender: 135 MB
GraalVM-receiver: 133 MB

### Startup time

JVM-sender: 2.265s
JVM-receiver: 2.135s

GraalVM-sender: 0.249s
GraalVM-receiver: 0.246s

### Memory consumption of pod

GraalVM: ca. 64Mib for ca. 200 requests per second
JVM: ca. 128Mib for ca. 150 requests per second

## Info for trying non-containerized native builds

```bash
# assumes sdkman for the next command
sdk install java 23.1.8.r21-nik
native-image --version
```

## Build natively
```bash
./gradlew nativeCompile
```

Find the executable, execute it.