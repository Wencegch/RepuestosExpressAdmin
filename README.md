# RepuestosExpressAdmin

RepuestosExpressAdmin es una aplicación móvil diseñada para la gestión de la venta de partes de coches. La aplicación está destinada a administradores que pueden gestionar productos, pedidos y sugerencias de manera eficiente. A continuación se describen las características y funcionalidades de la aplicación.

## Funcionalidades Principales

### Pantalla de Inicio de Sesión

- **Inicio de sesión con email**: Los administradores pueden iniciar sesión utilizando su dirección de correo electrónico y contraseña.
- **Permisos de Administrador**: Solo los usuarios con permisos de administrador, asignados desde la base de datos, pueden acceder.
- **Checkbox para recordar correo**: Permite guardar solo el correo electrónico para evitar tener que ingresarlo repetidamente, sin guardar la contraseña para mayor seguridad.
- **Botón de Iniciar Sesión**: Permite al administrador iniciar sesión en la aplicación.

### Frames Principales

#### Frame de Sugerencias

- **Listado de Productos**: Muestra todos los productos disponibles en la aplicación.
- **Selección de Productos**: Permite seleccionar un mínimo de 2 productos para ser mostrados en la sección de sugerencias del usuario.

#### Frame de Pedidos

- **ViewPager2 con Pedidos Pendientes e Historial**:
  - **Pedidos Pendientes**: Muestra los pedidos que aún no han sido finalizados.
    - **Cancelar Pedido**: Permite cancelar los pedidos pendientes.
    - **Finalizar Pedido**: Permite marcar un pedido como finalizado.
  - **Historial de Pedidos**: Muestra los pedidos finalizados. No se pueden borrar ni modificar.

#### Frame de Productos

- **Listado de Familias de Productos**: Muestra todas las familias de productos.
- **Crear Nueva Familia**: Botón de "+" en la esquina superior derecha para agregar una nueva familia de productos.
- **Gestión de Productos**:
  - **Ver Productos por Familia**: Al pulsar una familia, se muestran todos los productos asociados.
  - **Añadir Producto**: Botón de "+" para agregar un nuevo producto dentro de una familia.
  - **Borrar Producto**: Mantener pulsado un producto para borrarlo.
  - **Borrar Familia**: Mantener pulsada una familia para borrarla junto con todos los productos asociados.

## Uso de la Aplicación

### Iniciar Sesión

- Utilice sus credenciales de correo electrónico y contraseña para iniciar sesión.
- Asegúrese de tener permisos de administrador asignados desde la base de datos.
- Marque la casilla para recordar su correo electrónico si lo desea, pero recuerde que no se guardará la contraseña por motivos de seguridad.

### Gestionar Sugerencias

- Navegue al frame de sugerencias para ver todos los productos disponibles.
- Seleccione al menos 2 productos que desee mostrar en la sección de sugerencias para los usuarios.

### Gestionar Pedidos

- Vaya al frame de pedidos para ver los pedidos pendientes y el historial de pedidos.
- En la sección de pedidos pendientes, cancele o finalice los pedidos según sea necesario.
- Revise el historial de pedidos finalizados, que no se pueden modificar ni borrar.

### Gestionar Productos y Familias

- Vaya al frame de productos para ver todas las familias de productos.
- Cree nuevas familias de productos usando el botón "+" en la esquina superior derecha.
- Pulse sobre una familia para ver y gestionar los productos dentro de ella.
- Añada nuevos productos usando el botón "+" dentro de la familia seleccionada.
- Mantenga pulsado un producto o una familia para borrarlos. Al borrar una familia, todos sus productos asociados también se borrarán.

RepuestosExpressAdmin está diseñado para ofrecer a los administradores una herramienta potente y fácil de usar para la gestión de productos y pedidos, asegurando un control eficiente y seguro de la aplicación.
