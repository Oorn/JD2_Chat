# JD2_Chat
App for chatting.
Create new user with registration-controller with email confirmation. Admin controller has method for user creation without confirmation. All endpoints except registration and authentication require JWT token.
Create profiles to describe yourself and discover profiles of other users with profile-controller, start private chats with them.
Posting, editing and retreiving message is done with messsage controller. getLatest, hetBefore, and getAfter endpoints are used to traverse message history by message post date. getUpdatesAfter can be used to retrieve all new messages and updates, if any, that happened after specific moment in time.
Create group channels and invite other people to join them with channel controller. 
You can block specific other users from interacting with you with block controller.
//not yet implemented - leaving and kicking people out of group channels, friends functionality, multi-use group channels invites via browser link.
