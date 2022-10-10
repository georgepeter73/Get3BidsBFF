How to Generate the Grant_Token and then access token
----------------------------------------------------
Site for grant token : https://api-console.zoho.com
Scope = ZohoCRM.modules.ALL,ZohoCRM.users.ALL,ZohoCRM.settings.ALL,ZohoCRM.org.ALL,ZohoCRM.bulk.ALL
Scope Description: Users Modules Settings
Steps:
-----
Use the grant token to generate access token and refresh token .
access token expires in 1 hours . use the refresh token and keep refreshing the access token .
Use the latest access token to run the job.

tewst
