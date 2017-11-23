#### Things to do before Before you can compile and run the app

- Checkout `build.gradle` of the `root` project, and note that you need to add `maven` url for `Haptik SDK`, `TIL SDK` & `PhonePe SDK`.
- Get `AAR` of `AmazonPay` SDK from `Haptik`, and put it inside `PayWithAmazon` folder. Make sure that the name of the file is correctly
referenced from `build.gradle` of `PayWithAmazon` module!
- Checkout `string-haptik.xml` file in resource. Get `clientId`, `apiKey`, and `baseUrl` from `Haptik`
- For `PhonePe SDK` to work, you need to add meta-data in your `AndroidManifest.xml`. Ask `Haptik` for that information

#### Things to do after you successfully compile and run the app

- Go to `ClientActivity` and find `SignUpData.Builder`. Depending on what kind of `AuthType` you're using, make necessary changes to the `SignUpData` object builder and provide correct values. Run the app, and check it out!
