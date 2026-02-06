import { useEffect, useState } from "react";
import superagent from "superagent";
import { YMaps, Map, Placemark } from "@pbe/react-yandex-maps";
import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";


export function MyMap() {
    const [errorMessage, setErrorMessage] = useState('');
    const [vendingPoints, setVendingPoints] = useState([]);
    useEffect(() => {
        superagent
            .get('/api/open/vending_point/get_all')
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const responseVendingPoints = result.body;
                    if (responseVendingPoints === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setVendingPoints(responseVendingPoints);
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
    const createPlacemark = (vendingPoint) => {
        const vendingPointCords = vendingPoint['vendingPointCords'];
        const vendingPointAddress = vendingPoint['vendingPointAddress'];
        if (vendingPointCords === undefined || vendingPointAddress === undefined) {
            return;
        }
        return <Placemark modules={["geoObject.addon.balloon"]} defaultGeometry={vendingPointCords}
                          properties={{balloonContent: "Адрес: " + vendingPointAddress,}}/>;
    };
    return (
        <div>
            <Header/>
            <div id="wrapper" className="container">
                <YMaps>
                    <div>
                        <Map defaultState={{ center: [59.938678, 30.314474], zoom: 10,
                            controls: ["zoomControl", "fullscreenControl"],}} width={800} height={500}
                             modules={["control.ZoomControl", "control.FullscreenControl"]}>
                            <div>
                                {vendingPoints.map(vendingPoint => (createPlacemark(vendingPoint)))}
                            </div>
                        </Map>
                        <div className="error">{errorMessage}</div>
                    </div>
                </YMaps>
            </div>
            <Footer/>
        </div>
    );
}