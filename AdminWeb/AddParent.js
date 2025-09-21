// Import the functions you need from the SDKs you need
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-app.js";
import { getAuth, createUserWithEmailAndPassword, updateProfile } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-auth.js";
import { getDatabase, ref, set } from "https://www.gstatic.com/firebasejs/10.8.1/firebase-database.js";

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
const auth = getAuth();
const database = getDatabase();

window.register = function(e) {
  e.preventDefault();
  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;
  var name = document.getElementById("name").value;
  var sname = document.getElementById("sname").value;
  var grade = document.getElementById("grade").value;
  var address = document.getElementById("address").value;
  var contact = document.getElementById("contact").value;

  createUserWithEmailAndPassword(auth, email, password)
    .then((userCredential) => {
      // User creation successful, now update user profile with additional information
      updateProfile(auth.currentUser, {
        displayName: name
      }).then(() => {
        // Additional profile information updated successfully

        // Store additional information in the Realtime Database
        const userId = auth.currentUser.uid;
        const userRef = ref(database, 'users/' + userId);
        set(userRef, {
          name: name,
          email: email,
          sname: sname,
          grade: grade,
          address: address,
          contact: contact
        }).then(() => {
          alert("Registration Successful");

          // Clear the input values
          document.getElementById("name").value = "";
          document.getElementById("sname").value = "";
          document.getElementById("email").value = "";
          document.getElementById("password").value = "";
          document.getElementById("grade").value = "";
          document.getElementById("address").value = "";
          document.getElementById("contact").value = "";

        }).catch((error) => {
          alert("Error storing additional information: " + error.message);
        });

      }).catch((error) => {
        alert("Error updating profile: " + error.message);
      });
    })
    .catch(function(err) {
      alert("Error: " + err.message);
    });
};
