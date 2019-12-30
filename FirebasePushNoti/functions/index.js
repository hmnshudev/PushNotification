'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotifications = functions.firestore.document("Users/{user_id}/Notifications/{notification_id}").onWrite((change, context) => {
	
	const user_id = context.params.user_id;
	const notification_id = context.params.notification_id;
	
	//console.log("User ID : " + user_id + "|Notification Id :" + notification_id);
	return admin.firestore().collection("Users").doc(user_id).collection("Notifications").doc(notification_id).get().then(queryResult => {
		
		const from_user_id=queryResult.data().from;
		const from_message=queryResult.data().message;
		
		const from_data=admin.firestore().collection("Users").doc(from_user_id).get();
		const to_data=admin.firestore().collection("Users").doc(user_id).get();
		
		return Promise.all([from_data,to_data]).then(result => {
			
			const from_name=result[0].data().name;
			const to_name=result[1].data().name;
			const token_id=result[1].data().token_id;
			
			//console.log("From : " + from_name + " To: " + to_name + "Token ID: " + token_id);
			
			const payload={
				notification: {
					title: from_name,
					body: from_message,
					icon: "default",
					click_action: "com.push.notification.TARGETNOTIFICATION"
				},
				data: {
					message: from_message,
					from_user_id: from_user_id
				}
			};
			
			return admin.messaging().sendToDevice(token_id,payload).then(result => {
				console.log("Notification Sent from (" + from_name + ") to (" + to_name + ") - Message: " + from_message);
				return 0;
			});
		});
	});
});