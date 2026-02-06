import { useEffect, useState } from "react";
import superagent from "superagent";
import {useNavigate, useParams} from "react-router-dom";
import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";

export function FileSaving() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');
    const [downloadingMessage, setDownloadingMessage] = useState('Файл скачивается...');
    useEffect(() => {
        superagent
            .get('/api/file/get/' + id)
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const file = result.body;
                    if (file === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    returningFile(file);
                    setDownloadingMessage('Файл скачан');
                    setTimeout(() => navigate('/main'), 2000);
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
    const base64ToArrayBuffer = (base64) => {
        const binaryString = window.atob(base64);
        const binaryLen = binaryString.length;
        const bytes = new Uint8Array(binaryLen);
        for (let i = 0; i < binaryLen; i++) {
            const ascii = binaryString.charCodeAt(i);
            bytes[i] = ascii;
        }
        return bytes;
    };
    const saveByteArray = (fileName, fileType, byteArray) => {
        const file = new Blob([byteArray], { type: 'application/octet-stream' });
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(file);
        link.download = fileName;
        link.click();
    };
    const returningFile = (fileInfo) => {
        const fileId = fileInfo['fileId'];
        const userId = fileInfo['userId'];
        const fileName = fileInfo['fileName'];
        const fileLoadDatetime = fileInfo['fileLoadDatetime'];
        const fileContent = fileInfo['fileContent'];
        if (fileId === undefined ||
            userId === undefined ||
            fileName === undefined ||
            fileLoadDatetime === undefined ||
            fileContent === undefined) {
            return;
        }
        const sampleArray = base64ToArrayBuffer(fileContent);
        saveByteArray(fileName, '', sampleArray);
    };
    return (
        <div>
            <Header/>
            <div id="wrapper" className="container">
                <div id="content">
                    {downloadingMessage}
                    <div className="error">{errorMessage}</div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
