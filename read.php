<?php
header('Content-Type: application/json');

$host = "localhost";
$user = "root";
$pass = "";
$db   = "db_mahasiswa";

$conn = new mysqli($host, $user, $pass, $db);

$result = $conn->query("SELECT id, nama, nim, jurusan FROM mahasiswa");
$data = [];

while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);
$conn->close();
?>
