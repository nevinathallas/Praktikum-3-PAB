<?php
header('Content-Type: application/json');

$host = "localhost";
$user = "root";
$pass = "";
$db   = "db_mahasiswa";

$conn = new mysqli($host, $user, $pass, $db);

$id = $_POST['id'];
$nama = $_POST['nama'];
$nim = $_POST['nim'];
$jurusan = $_POST['jurusan'];

$stmt = $conn->prepare("UPDATE mahasiswa SET nama=?, nim=?, jurusan=? WHERE id=?");
$stmt->bind_param("sssi", $nama, $nim, $jurusan, $id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Data berhasil diupdate"]);
} else {
    echo json_encode(["success" => false, "message" => "Gagal mengupdate data"]);
}

$stmt->close();
$conn->close();
?>
