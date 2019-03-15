const alertModal = $('#alert-model');
const alertText = alertModal.find('.alert-body-text');

function alert(content) {
    alertText.text(content);
    alertModal.modal();
}