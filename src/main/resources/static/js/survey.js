function deleteSurvey(endpoint, id) {
    if (confirm("Bạn chắc chắn xóa?") === true) {
        fetch(`${endpoint}/${id}`, {
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
