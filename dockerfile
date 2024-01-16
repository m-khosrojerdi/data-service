stages:
  - build
variables:
  GIT_STRATEGY: clone
  TAG_LATEST: nexus.mapnamind.com/data-service:latest
  TAG_COMMIT: nexus.mapnamind.com/data-service:$CI_COMMIT_SHORT_SHA
build:
 stage: build
 script:
   - ls -l
   - docker build . -t $TAG_LATEST
   - docker image tag $TAG_LATEST $TAG_COMMIT
   - docker image push $TAG_LATEST
   - docker image push $TAG_COMMIT
   - docker image rm --force $TAG_LATEST $TAG_COMMIT
