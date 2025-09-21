import { getAuth, signInWithEmailAndPassword } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-auth.js';
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-app.js';

// Replace this with your Firebase project configuration
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

// Get the Auth instance
const auth = getAuth();

// Login function
window.login = function() {
    // Get email and password from the input fields
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Use Firebase Auth to sign in with email and password
    signInWithEmailAndPassword(auth, email, password)
        .then((userCredential) => {
            // Signed in
            const user = userCredential.user;
            console.log('User signed in:', user);
            alert('Login Successful!');
            // Add your logic after successful login, e.g., redirect to another page
            window.location.href = "AdminHome.html";
        })
        .catch((error) => {
            // Handle errors during login
            const errorCode = error.code;
            const errorMessage = error.message;
            console.error('Login error:', errorCode, errorMessage);
            alert('Login Unsuccessful. Please Check Your Email And Password.');
        });
}

// Toggle password visibility
document.getElementById('togglePassword').addEventListener('click', function() {
    const passwordField = document.getElementById('password');
    const type = passwordField.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordField.setAttribute('type', type);
});
