import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getDatabase, ref, set, onValue } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-database.js";

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

const urlParams = new URLSearchParams(window.location.search);
const userEmail = urlParams.get('email');

const editUserForm = document.getElementById('editUserForm');

// Read user details from the database
const userRef = ref(database, 'teachers/' + userEmail);
onValue(userRef, (snapshot) => {
    const userData = snapshot.val();
    if (userData) {
        // Populate form fields with user data
        document.getElementById('name').value = userData.name;
        document.getElementById('email').value = userData.email;
        document.getElementById('grade').value = userData.grade;
        document.getElementById('address').value = userData.address;
        document.getElementById('contact').value = userData.contact;
    } else {
        alert('User Not Found.');
    }
});

// Update user details
editUserForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const updatedUserData = {
        name: editUserForm.name.value,
        email: editUserForm.email.value,
        grade: editUserForm.grade.value,
        address: editUserForm.address.value,
        contact: editUserForm.contact.value
    };

    // Update user data in the database
    const userRef = ref(database, 'teachers/' + userEmail);
    set(userRef, updatedUserData)
        .then(() => {
            alert('User Details Updated Successfully.');
            editUserForm.reset();
            window.location.href = 'ManageTeacher.html';
        })
        .catch((error) => {
            console.error('Error updating user details:', error);
            alert('Error Updating User Details.');
        });
});
