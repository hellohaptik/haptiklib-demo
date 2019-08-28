#### Things to do before Before you can compile and run the app

- Checkout `build.gradle` of the `root` project, and note that you need
  to add `maven` url for `Haptik SDK`.
- Checkout `string-haptik.xml` file in resource. Get `clientId`, and
  `baseUrl` from `Haptik`

#### Things to do after you successfully compile and run the app

- Go to `ClientHomeActivity` and find `SignUpData.Builder`. Depending on
  what kind of `AuthType` you're using, make necessary changes to the
  `SignUpData` object builder and provide correct values. Run the app,
  and check it out!
  
#### For configuring Notifications

- Checkout the `MyFirebaseMessagingService.java` file, to see how to
  enable notifications in Haptik SDK

#### Read documentation for further details  
- Visit the documentation at docs.haptik.ai  
