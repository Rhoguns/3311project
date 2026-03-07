# 3311project
# url: [https://github.com/Rhoguns/3311project](https://github.com/Rhoguns/3311project)

Which design patterns were used and where:
	The admin code uses the Singleton pattern and the Command pattern. The singleton pattern is used in Admin.java to ensure only one instance of an admin is allowed at all times. This pattern is also used for the system policies since these should only be one of each og them system wide. For example CancellationPolicy.java uses the singleton pattern, so does NotificationPolicy, etc. The Command Pattern is used for configuring
the policies and is used in the PolicyCommand.java interface and flies like CancellationPolicyCommand, PricingPolicyCommand, etc. 
