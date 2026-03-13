function validateForm() {

    let email = document.getElementById("email").value;
    let pass = document.getElementById("password").value;

    if(email === "" || pass === ""){
        alert("All fields required");
        return false;
    }

    return true;
}
