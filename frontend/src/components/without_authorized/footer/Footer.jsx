import { Link, NavLink } from "react-router-dom";


export function Footer() {
    return (
        <div id="footer" className="container">
            <div className="column">
                <h3>О компании</h3>
                <ul className="menu menu-column">
                    <li><NavLink to="#">Реквизиты</NavLink></li>
                    <li><NavLink to="#">Политика<br/>конфиденциальности</NavLink></li>
                    <li><NavLink to="#">Публичная оферта</NavLink></li>
                </ul>
                <h5>©2023 Все права защищены</h5>
            </div>
            <div className="column">
                <h3>Навигация</h3>
                <ul className="menu menu-column">
                    <li><NavLink to="#">О нас</NavLink></li>
                    <li><NavLink to="#">Помощь</NavLink></li>
                    <li><NavLink to="/map_without_login">Мы на карте</NavLink></li>
                    <li><NavLink to="/login">Вход</NavLink></li>
                </ul>
            </div>
            <div className="column">
                <h3>Наши соцсети</h3>
                <ul className="menu">
                    <li><Link to="#"><img src={"/img/VK_Compact_Logo_(2021-present).svg.webp"} alt="vk" /></Link></li>
                    <li><Link to="#"><img src={"/img/Telegram_2019_Logo.svg.png"} alt="telegram" /></Link></li>
                </ul>
                <h4>Email: info@typofast.ru</h4>
            </div>
        </div>
    );
}