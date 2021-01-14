import moment from "moment";
// todo Find a way to import like ts file
export const formatDate = value => moment(value).format(" h:mm A, MMM D, YYYY");
