import { Route, Routes } from "react-router-dom";
/* Страницы для неавторизованного пользователя */
import { Home as HomeWithoutLogin } from "./components/without_authorized/page/Home";
import { Login } from "./components/without_authorized/page/Login";
import { Register } from "./components/without_authorized/page/Register";
import { MyMap as  MapWithoutAuthorized } from "./components/without_authorized/page/MyMap";
/* Страницы для авторизованного пользователя */
import { Home } from "./components/authorized/page/Home";
import { MyMap } from "./components/authorized/page/MyMap";
import { NewOrderPrint } from "./components/authorized/page/NewOrderPrint";
import { NewOrderScan } from "./components/authorized/page/NewOrderScan";
import { OrderPrint } from "./components/authorized/page/OrderPrint";
import { OrderScan } from "./components/authorized/page/OrderScan";
import { Cart } from "./components/authorized/page/Cart";
import { Files } from "./components/authorized/page/Files";
import { FileSaving } from "./components/authorized/page/FileSaving";
import { Replenishes } from "./components/authorized/page/Replenishes";
import { NewReplenish } from "./components/authorized/page/NewReplenish";

export function App() {
    return (
        <Routes>
            {/* Страницы для неавторизованного пользователя */}
            <Route exact path="/" element={<HomeWithoutLogin/>}/>
            <Route path="/login" element={<Login/>}/>
            <Route path="/register" element={<Register/>}/>
            <Route path="/map_without_login" element={<MapWithoutAuthorized/>}/>
            {/* Страницы для авторизованного пользователя */}
            <Route path="/main" element={<Home/>}/>
            <Route path="/map" element={<MyMap/>}/>
            <Route path="/new_order_print" element={<NewOrderPrint/>}/>
            <Route path="/new_order_scan" element={<NewOrderScan/>}/>
            <Route path="/order_print/:id" element={<OrderPrint/>}/>
            <Route path="/order_scan/:id" element={<OrderScan/>}/>
            <Route path="/cart" element={<Cart/>}/>
            <Route path="/files" element={<Files/>}/>
            <Route path="/file/:id" element={<FileSaving/>}/>
            <Route path="/replenishes" element={<Replenishes/>}/>
            <Route path="/new_replenish" element={<NewReplenish/>}/>
        </Routes>
    );
}
