<?php
header('Content-Type: application/json');

$host = "localhost";
$user = "root";
$pass = "";
$db   = "db_mahasiswa";

$conn = new mysqli($host, $user, $pass, $db);

$id = $_GET['id'];

$stmt = $conn->prepare("DELETE FROM mahasiswa WHERE id=?");
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Data berhasil dihapus"]);
} else {
    echo json_encode(["success" => false, "message" => "Gagal menghapus data"]);
}

$stmt->close();
$conn->close();
?>