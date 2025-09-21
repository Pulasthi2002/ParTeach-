// Import necessary Firebase modules
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getDatabase, ref, onValue, remove, push} from "https://www.gstatic.com/firebasejs/10.8.1/firebase-database.js";

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyDM6dKxDY61g3KS1xozyA2Cx0HVpHc7ER4",
    authDomain: "par-teach-mobile-app.firebaseapp.com",
    databaseURL: "https://par-teach-mobile-app-default-rtdb.firebaseio.com",
    projectId: "par-teach-mobile-app",
    storageBucket: "par-teach-mobile-app.appspot.com",
    messagingSenderId: "464003368122",
    appId: "1:464003368122:web:dc41f3ecf9ed5387f9ae83"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const database = getDatabase();

// Get reference to the user list container
const userListDiv = document.getElementById('userList');

// Function to display users
function displayUsers(users) {
    userListDiv.innerHTML = '';

    users.forEach((user) => {
        const userData = user.val();
        const userId = user.key;

        Object.keys(userData).forEach((key) => {
            const feedbackData = userData[key];
            const userEmail = feedbackData.email;
            const feedbackMessage = feedbackData.message;
            const dateTime = new Date(key); // Convert key to date object

            const userElement = document.createElement('div');
            userElement.classList.add('user');

            userElement.innerHTML = `
                <p><strong>Date & Time:</strong> ${dateTime.toLocaleString()}</p>
                <p><strong>Email:</strong> ${userEmail}</p>
                <p><strong>Message:</strong> ${feedbackMessage}</p>
                <button class="delete-btn" data-userid="${userId}" data-feedbackid="${key}">Done</button>
                <hr>
            `;
            userListDiv.appendChild(userElement);
        });
    });

    // Attach event listeners to delete and send reply buttons
    const deleteButtons = document.querySelectorAll('.delete-btn');
    

    deleteButtons.forEach((button) => {
        button.addEventListener('click', handleDelete);
    });

}

// Read users from the database
const usersRef = ref(database, 'parent_support');
onValue(usersRef, (snapshot) => {
    const users = [];
    snapshot.forEach((childSnapshot) => {
        users.push(childSnapshot);
    });
    displayUsers(users);
});

// Function to handle delete button click
function handleDelete(event) {
    const userId = event.target.dataset.userid;
    const feedbackId = event.target.dataset.feedbackid;
    // Implement delete functionality here
    remove(ref(database, `parent_support/${userId}/${feedbackId}`));
    console.log('Delete feedback with User ID:', userId, 'Feedback ID:', feedbackId);
}
