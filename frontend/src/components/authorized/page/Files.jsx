import { useEffect, useState } from "react";
import superagent from "superagent";
import { Link } from "react-router-dom";
import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";


export function Files() {
    const [errorMessage, setErrorMessage] = useState('');
    const [filesAttachedScanOrder, setFilesAttachedScanOrder] = useState([]);
    const [filesAttachedPrintOrder, setFilesAttachedPrintOrder] = useState([]);
    useEffect(() => {
        superagent
            .get('/api/file/get_all_scan')
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const files = result.body;
                    if (files === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setFilesAttachedScanOrder(files);
                    return true;
                }
            )
            .catch((err) => {
                    const responseMessage = err.response.body['responseMessage'];
                    if (responseMessage === undefined) {
                        return false;
                    }
                    setErrorMessage(responseMessage);
                    return false;
                }
            );
        superagent
            .get('/api/file/get_all_print')
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const files = result.body;
                    if (files === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setFilesAttachedPrintOrder(files);
                    return true;
                }
            )
            .catch((err) => {
                    const responseMessage = err.response.body['responseMessage'];
                    if (responseMessage === undefined) {
                        return false;
                    }
                    setErrorMessage(responseMessage);
                    return false;
                }
            );
    }, []);
    const createFileElement = (fileInfo) => {
        const fileId = fileInfo['fileId'];
        const userId = fileInfo['userId'];
        const fileName = fileInfo['fileName'];
        const fileLoadDatetime = fileInfo['fileLoadDatetime'];
        if (fileId === undefined ||
            userId === undefined ||
            fileName === undefined ||
            fileLoadDatetime === undefined) {
            return;
        }
        return (
            <div className="file">
                <img src={"/img/file.png"} alt="printer"/>
                <Link to={"/file/" + fileId}>{fileName}</Link>
                <div>{fileLoadDatetime}</div>
            </div>
        );
    };
    return (
        <div>
            <Header/>
            <div id="wrapper" className="container">
                <div id="content">
                    <div className="container column">
                        <h3>Файлы полученные сканированием</h3>
                        <div className="container files">
                            {filesAttachedScanOrder.map(fileInfo => createFileElement(fileInfo))}
                        </div>
                    </div>
                    <div className="container column">
                        <h3>Загруженные файлы</h3>
                        <div className="container files">
                            {filesAttachedPrintOrder.map(fileInfo => createFileElement(fileInfo))}
                        </div>
                    </div>
                    <div className="error">{errorMessage}</div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
