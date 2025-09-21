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

// Check if the email parameter is present in the URL
const urlParams = new URLSearchParams(window.location.search);
const userEmail = urlParams.get('email');

// Check if userEmail is not null, otherwise display an error message
if (userEmail) {
    // Encode the email to use it as a key in the database
    const encodedEmail = encodeURIComponent(userEmail);
    const userRef = ref(database, 'users/' + encodedEmail);

    // Read user details from the database
    onValue(userRef, (snapshot) => {
        const userData = snapshot.val();
        if (userData) {
            // Populate form fields with user data
            document.getElementById('name').value = userData.name;
            document.getElementById('sname').value = userData.sname;
            document.getElementById('email').value = userData.email;
            document.getElementById('grade').value = userData.grade;
            document.getElementById('address').value = userData.address;
            document.getElementById('contact').value = userData.contact;
        } else {
            alert('User not found.');
        }
    });

    const editUserForm = document.getElementById('editUserForm');

    // Update user details
    editUserForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const updatedUserData = {
            name: editUserForm.name.value,
            sname: editUserForm.sname.value,
            email: editUserForm.email.value,
            grade: editUserForm.grade.value,
            address: editUserForm.address.value,
            contact: editUserForm.contact.value
        };

        // Update user data in the database
        set(userRef, updatedUserData)
            .then(() => {
                alert('User Details Updated Successfully.');
                editUserForm.reset();
                window.location.href = 'ManageParent.html';
            })
            .catch((error) => {
                console.error('Error updating user details:', error);
                alert('Error Updating User Details.');
            });
    });
} else {
    alert('Email parameter not found in the URL.');
}
