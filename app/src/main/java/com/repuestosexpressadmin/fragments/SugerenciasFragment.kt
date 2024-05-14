import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

class SugerenciasFragment : Fragment() {
    private lateinit var productosAdapter: RecyclerAdapterProductos
    private lateinit var recyclerView: RecyclerView
    private lateinit var productos: ArrayList<Producto>
    private var posicionPulsada: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sugerencias, container, false)

        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.sugerencias)
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.color.green))
        }


        productos = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerViewSugerencias)
        recyclerView.layoutManager = LinearLayoutManager(context)
        productosAdapter = RecyclerAdapterProductos(productos)
        recyclerView.adapter = productosAdapter

        Firebase().obtenerProductos { listaProductos ->
            productos.addAll(listaProductos)
            productosAdapter.notifyDataSetChanged()
        }

        productosAdapter.setOnItemClickListener(object : RecyclerAdapterProductos.OnItemClickListener {
            override fun onItemClick(position: Int) {
                posicionPulsada = position
                val productoSeleccionado = productos[posicionPulsada]
                if (productoSeleccionado.selected) {
                    productoSeleccionado.selected = false
                } else {
                    productoSeleccionado.selected = true
                }
                productosAdapter.notifyItemChanged(posicionPulsada)
            }
        })

        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.btn_EnviarSugerencias -> {
                val idsProductosSeleccionados = mutableListOf<String>()
                for (producto in productos) {
                    if (producto.selected) {
                        idsProductosSeleccionados.add(producto.id)
                    }
                }
                Firebase().actualizarSugerencias(idsProductosSeleccionados)
                Utils.Toast(requireContext(), getString(R.string.sugerencias_enviadas))
                productosAdapter.deseleccionarTodos()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.suggest_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
