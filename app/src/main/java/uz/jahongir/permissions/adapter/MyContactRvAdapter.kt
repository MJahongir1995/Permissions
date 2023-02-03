package uz.jahongir.permissions.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.jahongir.permissions.databinding.ItemRvBinding
import uz.jahongir.permissions.models.MyContacts
import java.lang.ref.WeakReference
import java.util.*

class MyContactRvAdapter(var list: ArrayList<MyContacts>) : RecyclerView.Adapter<MyContactRvAdapter.VH>() {
    inner class VH(private var itemRvBinding: ItemRvBinding) : RecyclerView.ViewHolder(itemRvBinding.root) {
        fun onBind(myContacts: MyContacts) {
            itemRvBinding.tvName.text = myContacts.name
            itemRvBinding.tvNumber.text = myContacts.number

            if (myContacts.image!= null){
                itemRvBinding.image.setImageBitmap(myContacts.image)
            }
        }
        private val view = WeakReference(itemRvBinding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun getContactAt(position: Int): MyContacts {
        return list[position]
    }
}