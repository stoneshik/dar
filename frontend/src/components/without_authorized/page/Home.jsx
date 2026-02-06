import { Link } from "react-router-dom";

import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";


export function Home() {
    return (
        <div>
            <Header/>
            <div id="wrapper_index_page">
                <div id="index_page_header">
                    <div className="form-row">
                        <h1>Вендинг печати и сканирования</h1>
                        <div id="button_wrapper" className="container">
                            <Link to="/register">
                                <div id="button_first" className="button">Начать пользоваться</div>
                            </Link>
                            <Link to="/login">
                                <div id="button_second" className="button">Уже есть аккаунт</div>
                            </Link>
                        </div>
                    </div>
                </div>
                <div id="index_page_content">
                    <div className="container column">
                        <h2>Наши преимущества</h2>
                        <div className="container">
                            <div className="advantage">
                                <img src={"/img/speed.png"} alt="speed"/>
                                <h3>Скорость</h3>
                            </div>
                            <div className="advantage">
                                <img src={"/img/click.png"} alt="click"/>
                                <h3>Оформление заказа<br/>в пару кликов</h3>
                            </div>
                            <div className="advantage">
                                <img src={"/img/discount.png"} alt="discount"/>
                                <h3>Низкая цена</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
