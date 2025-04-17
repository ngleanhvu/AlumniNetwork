function deleteUser(endpoint, username) {
    if (confirm("Bạn chắc chắn xóa?") === true) {
        fetch(`${endpoint}/${username}`, {
            method: "delete"
        }).then(res => {
            if (res.status === 204) {
                alert("Xóa thành công!");
                location.reload();
            } else
                alert("Hệ thống bị lỗi!");
        });
    }
}

function confirmAlumniUser(endpoint, username) {
    fetch(`${endpoint}`, {
        method: "patch",
        body: JSON.stringify({
            username: `${username}`
        })
    }).then(res =>  {
        if(res.status === 200) {
            alert("Cập nhập thành công")
        } else
            alert("Hệ thống bị lỗi")
    })
}