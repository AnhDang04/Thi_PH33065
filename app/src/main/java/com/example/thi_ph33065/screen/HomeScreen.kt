package com.example.thi_ph33065.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import coil.compose.rememberImagePainter
import com.example.thi_ph33065.SanPhamDB
import com.example.thi_ph33065.SanPhamModel


@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // Khởi tạo cơ sở dữ liệu SanPhamDB
    val db = Room.databaseBuilder(
        context,
        SanPhamDB::class.java, "Com_Tam_1"
    ).allowMainThreadQueries().addMigrations(SanPhamDB.MIGRATION_1_2).build()

    var listSanPham by remember {
        mutableStateOf(db.sanphamDao().getAll())
    }

    var showDialog by remember { mutableStateOf(false) }
    // sua
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedSanPham by remember { mutableStateOf<SanPhamModel?>(null) }
    // xoa
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var sanPhamToDelete by remember { mutableStateOf<SanPhamModel?>(null) }
    // chi tiet
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedSanPhamDetail by remember { mutableStateOf<SanPhamModel?>(null) }


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listSanPham) { sanPham ->
                ProductItem(
                    sanPham = sanPham,
                    onEdit = {
                        selectedSanPham = it
                        showEditDialog = true
                    },
                    onDelete = {
                        sanPhamToDelete = it
                        showDeleteConfirmation = true
                    },
                    onClick = {
                        selectedSanPhamDetail = it
                        showDetailDialog = true
                    }
                )
            }
        }

        IconButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.Black)
        }
    }



    // Dialog them sản phẩm
if (showDialog) {
        AddProductDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, price, description, status, image ->
                val newSanPham = SanPhamModel(
                    name = name,
                    giaMonAn = price.toDouble(),
                    description = description,
                    status = status,
                    image = image
                )
                db.sanphamDao().insert(newSanPham)
                listSanPham = db.sanphamDao().getAll()
                showDialog = false
            }
        )
}

    // Dialog sửa sản phẩm
if (showEditDialog && selectedSanPham != null) {
        EditProductDialog(
            sanPham = selectedSanPham!!,
            onDismiss = { showEditDialog = false },
            onEdit = { name, price, description, status, image ->
                val updatedSanPham = selectedSanPham!!.copy(
                    name = name,
                    giaMonAn = price.toDouble(),
                    description = description,
                    status = status,
                    image = image
                )
                db.sanphamDao().update(updatedSanPham)
                listSanPham = db.sanphamDao().getAll()
                showEditDialog = false
            }
        )
}

    // Dialog xác nhận xóa sản phẩm
if (showDeleteConfirmation && sanPhamToDelete != null) {
        DeleteConfirmationDialog(
            sanPham = sanPhamToDelete!!,
            onConfirm = {
                db.sanphamDao().delete(sanPhamToDelete!!)
                listSanPham = db.sanphamDao().getAll()
                showDeleteConfirmation = false
                sanPhamToDelete = null
            },
            onDismiss = {
                showDeleteConfirmation = false
                sanPhamToDelete = null
            }
        )
}

    // Dialog chi tiết sản phẩm
if (showDetailDialog && selectedSanPhamDetail != null) {
        val sanPhamDetail = db.sanphamDao().getSanPhamById(selectedSanPhamDetail!!.uid)
        if (sanPhamDetail != null) {
            ProductDetailDialog(
                sanPham = sanPhamDetail,
                onDismiss = {
                    showDetailDialog = false
                    selectedSanPhamDetail = null
                }
            )
        }
    }
}

// item san pham
@Composable
fun ProductItem(
    sanPham: SanPhamModel,
    onEdit: (SanPhamModel) -> Unit,
    onDelete: (SanPhamModel) -> Unit,
    onClick: (SanPhamModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable{onClick(sanPham)} ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(sanPham.image),
            contentDescription = sanPham.name,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = sanPham.name)
            Text(text = "Price: ${sanPham.giaMonAn}")
            Text(text = "Trạng thái: ${if (sanPham.status) "Có sẵn" else "Hết hàng"}")
        }

        IconButton(onClick = { onEdit(sanPham) }) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Black)
        }

        IconButton(onClick = { onDelete(sanPham) }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Black)
        }
    }
}

// them san pham
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            imageError = uri == null
        }
    )

    fun validate(): Boolean {
        var isValid = true
        nameError = name.isBlank()
        priceError = price.isBlank() || price.toDoubleOrNull() == null
        descriptionError = description.isBlank()
        imageError = imageUri == null

        if (nameError || priceError || descriptionError || imageError) {
            isValid = false
        }
        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Product") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    label = { Text("Tên món ăn", color = Color(0xFFFFB703)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    isError = nameError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.Black,
                        containerColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )
                if (nameError) {
                    Text(
                        text = "Tên món ăn không được để trống",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        priceError = it.isBlank() || it.toDoubleOrNull() == null
                    },
                    label = { Text("Giá món ăn", color = Color(0xFFFFB703)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    isError = priceError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.Black,
                        containerColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )
                if (priceError) {
                    Text(
                        text = "Giá món ăn phải là số hợp lệ",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = it.isBlank()
                    },
                    label = { Text("Mô tả", color = Color(0xFFFFB703)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    isError = descriptionError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.Black,
                        containerColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )
                if (descriptionError) {
                    Text(
                        text = "Mô tả không được để trống",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = status,
                        onCheckedChange = { status = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFB703),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(text = "Available", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        pickImageLauncher.launch("image/*")
                    }
                ) {
                    Text(text = "Chọn ảnh")
                }
                imageUri?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(128.dp)
                    )
                }
                if (imageError) {
                    Text(
                        text = "Bạn phải chọn một ảnh",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validate()) {
                        onAdd(name, price, description, status, imageUri.toString())
                    }
                }
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            Button(onDismiss) {
                Text("Hủy")
            }
        }
    )
}

// sưa san pham
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(
    sanPham: SanPhamModel,
    onDismiss: () -> Unit,
    onEdit: (String, String, String, Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf(sanPham.name) }
    var price by remember { mutableStateOf(sanPham.giaMonAn.toString()) }
    var description by remember { mutableStateOf(sanPham.description) }
    var status by remember { mutableStateOf(sanPham.status) }
    var imageUri by remember { mutableStateOf(Uri.parse(sanPham.image)) }

    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            imageError = uri == null
        }
    )

    fun validate(): Boolean {
        var isValid = true
        nameError = name.isBlank()
        priceError = price.isBlank() || price.toDoubleOrNull() == null
        descriptionError = description.isBlank()

        if (nameError || priceError || descriptionError || imageError) {
            isValid = false
        }
        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Product") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    label = { Text("Tên món ăn", color = Color(0xFFFFB703)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    isError = nameError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.Black,
                        containerColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )
                if (nameError) {
                    Text(
                        text = "Tên món ăn không được để trống",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        priceError = it.isBlank() || it.toDoubleOrNull() == null
                    },
                    label = { Text("Giá món ăn", color = Color(0xFFFFB703)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    isError = priceError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.Black,
                        containerColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )
                if (priceError) {
                    Text(
                        text = "Giá món ăn phải là số hợp lệ",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = it.isBlank()
                    },
                    label = { Text("Mô tả", color = Color(0xFFFFB703)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    textStyle = TextStyle(fontSize = 13.sp),
                    isError = descriptionError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.Black,
                        containerColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )
                if (descriptionError) {
                    Text(
                        text = "Mô tả không được để trống",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = status,
                        onCheckedChange = { status = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFB703),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(text = "Available", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        pickImageLauncher.launch("image/*")
                    }
                ) {
                    Text(text = "Chọn ảnh")
                }
                imageUri?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(128.dp)
                    )
                }
                if (imageError) {
                    Text(
                        text = "Bạn phải chọn một ảnh",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validate()) {
                        onEdit(name, price, description, status, imageUri.toString())
                    }
                }
            ) {
                Text("Sửa")
            }
        },
        dismissButton = {
            Button(onDismiss) {
                Text("Hủy")
            }
        }
    )
}

// xoa san pham
@Composable
fun DeleteConfirmationDialog(
    sanPham: SanPhamModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xác nhận xóa") },
        text = { Text("Bạn có chắc chắn muốn xóa sản phẩm '${sanPham.name}' không?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Hủy")
            }
        }
    )
}

// chitiet
@Composable
fun ProductDetailDialog(
    sanPham: SanPhamModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Chi tiết sản phẩm",
                style = TextStyle(fontWeight = FontWeight.Bold),
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Image(
                    painter = rememberImagePainter(sanPham.image),
                    contentDescription = sanPham.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tên sản phẩm: ${sanPham.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Giá: ${sanPham.giaMonAn}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mô tả: ${sanPham.description}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Trạng thái: ${if (sanPham.status) "Có sẵn" else "Hết hàng"}",
                    fontSize = 16.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = "Đóng")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}
