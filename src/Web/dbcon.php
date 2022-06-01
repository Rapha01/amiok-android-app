<?php
require_once("../inc/phpmailer/PHPMailerAutoload.php");

$dbcon = new mysqli("localhost", "root", "", "amiok")
	or die('Verbindungsaufbau fehlgeschlagen: ' . $dbcon->connect_error);

//$dbcon->query("SET time_zone = '+00:00'");
//date_default_timezone_set ('Etc/UTC');
$curDateTime = date('Y-m-d H:i:s');

if(isset($_GET["id"]))
  $id = mysqli_real_escape_string($dbcon, $_GET["id"]);

if(isset($_GET["name"]))
  $name = mysqli_real_escape_string($dbcon, $_GET["name"]);

if(isset($_GET["password"]))
  $password = mysqli_real_escape_string($dbcon, $_GET["password"]);

if(isset($_GET["frequency"])) {
  $frequency = mysqli_real_escape_string($dbcon, $_GET["frequency"]);
	if ($frequency == "")
		$frequency = 0;
}

if(isset($_GET["email"]))
  $email = mysqli_real_escape_string($dbcon, $_GET["email"]);

if(isset($_GET["message"]))
  $message = mysqli_real_escape_string($dbcon, $_GET["message"]);

if(isset($_GET["trackedEntries"]))
	$trackedEntries = mysqli_real_escape_string($dbcon, $_GET["trackedEntries"]);

if(isset($_GET["newEntry"])) {

	$entries = array();

  if(isset($name) && isset($password) && isset($frequency)) {
    $res = $dbcon->query("SELECT name FROM entry WHERE name = '".$name."'");

		if ($password == "")
      $error = "No password set";

    if (!isset($email))
      $email = "";

    if (!isset($message))
      $message = "";

    if ($res->num_rows >= 1) {
      $error = "Name already in use";
    } else {
      $res = $dbcon->query("INSERT INTO entry (name,password,frequency,email,message,creationdate,lastupdate)
        VALUES ('".$name."','".$password."',".$frequency.",'".$email."','".$message."','".$curDateTime."','".$curDateTime."')");

        $entry = new stdClass();

        $entry->id = $dbcon->insert_id;
        $entries[] = $entry;

				if ($dbcon->error != "")
					$error = "Could not add entry";

    }
  } else {
		$error = "Not all values set";
	}

}

if(isset($_GET["searchEntry"])) {
	$entries = array();

  if(isset($name) && $name != "") {
    $res = $dbcon->query("SELECT id,name,frequency,email,message,creationdate,lastupdate FROM entry WHERE name LIKE '%{$name}%' ");

    if ($res->num_rows < 1) {
      $error = "Nothing found";
    } else {
				while ($entry = $res->fetch_object()) {
					  $entries[] = $entry;
				}
    }
  } else {

		$error = "No name set";
	}
}

if(isset($_GET["getEntry"])) {
	$entries = array();

  if(isset($id) && $id != "") {
  	$res = $dbcon->query("SELECT * FROM entry WHERE id = ".$id);

    if ($res->num_rows < 1) {
      $error = "Nothing found";
    } else {
				while ($entry = $res->fetch_object()) {
					  $entries[] = $entry;
				}
    }
  } else {
		$error = "No id set";
	}
}

if(isset($_GET["updateEntry"])) {
	$entries = array();

  if(isset($id) && $id != "" && isset($name) && $name != "" && isset($password) && $password != "" && isset($frequency) && isset($email) && isset($message)) {
  	$res = $dbcon->query("SELECT * FROM entry WHERE id = ".$id);

    if ($res->num_rows < 1) {
      $error = "Nothing found";
    } else {
			$entry = $res->fetch_object();

			if($entry->password == $password) {
				$res = $dbcon->query("UPDATE entry SET name = '".$name."', frequency = ".$frequency.", email = '".$email."', message = '".$message."', lastupdate = '".$curDateTime."', emailsent = 0  WHERE id = ".$id);

				$response = new stdClass();
				$response->success = "success";
				$entries[] = $response;
			} else {
				$error = "Wrong Password";
			}
    }
  } else {
		$error = "Not all values set";
	}
}

if(isset($_GET["getTrackedEntries"])) {
	$entries = array();


  if(isset($trackedEntries) && $trackedEntries != "") {
		$trackedEntries = json_decode($trackedEntries);

		foreach ($trackedEntries as $entryId) {
			$res = $dbcon->query("SELECT id,name,frequency,email,message,creationdate,lastupdate FROM entry WHERE id = ".$entryId);
			if ($res->num_rows >= 1) {
				$entry = $res->fetch_object();
				$entries[] = $entry;
			}
		}
	}
}

if (isset($error)) {
	$err = new stdClass();
	$err->error = $error;
	$entries[] = $err;
}

if(!empty($entries))
	echo json_encode($entries);


$res = $dbcon->query("SELECT lastemailcheck FROM admin WHERE id = 1");
if($res->fetch_object()->lastemailcheck < date("Y-m-d H:i:s", strtotime($curDateTime) - 2)) {
	$dbcon->query("UPDATE admin SET lastemailcheck = '".$curDateTime."' WHERE id = 1");
	$res = $dbcon->query("SELECT * FROM entry");

	while ($entry = $res->fetch_object()) {
		if ($entry->emailsent == 0 && $entry->lastupdate < date("Y-m-d H:i:s", strtotime($curDateTime) - $entry->frequency * 3600) && $entry->email != "") {
			$text = "<strong>Hi</strong><br />
					Your friend " . $entry->name . " set an alarm at $entry->lastupdate and didn't show up for " .$entry->frequency. " hours<br /><br/>
					The message from your friend: <br />" . $entry->message;

			$mail = new PHPMailer(); // create a new object
			$mail->IsSMTP(); // enable SMTP
			$mail->SMTPDebug = 0; // debugging: 1 = errors and messages, 2 = messages only
			$mail->SMTPAuth = true; // authentication enabled
			$mail->SMTPSecure = 'ssl'; // secure transfer enabled REQUIRED for Gmail
			$mail->Host = "smtp.gmail.com";
			$mail->Port = 465; // or 587
			$mail->IsHTML(true);

			// NOTE: Allow Less secure apps in google account
			$mail->Username = "amiokadm@gmail.com";
			$mail->Password = "youareok";
			$mail->SetFrom('amiokadm@gmail.com', 'Amiok');
			$mail->Subject = "Alert Message";
			$mail->Body = $text;
			$mail->AddAddress($entry->email);


			if(!$mail->Send()) {
				echo "Mailer Error: " . $mail->ErrorInfo;
			} else {
				 $dbcon->query("UPDATE entry SET emailsent = 1 WHERE id = " . $entry->id);
			}

		}
	}
}



?>
