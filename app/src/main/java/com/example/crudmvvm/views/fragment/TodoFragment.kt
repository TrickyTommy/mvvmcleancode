package com.example.crudmvvm.views.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crudmvvm.databinding.FragmentTodoBinding
import com.example.crudmvvm.model.TodoModel
import com.example.crudmvvm.viewmodels.StatesTodo
import com.example.crudmvvm.viewmodels.TodoViewModel
import com.example.crudmvvm.views.adapter.TodoAdapter
import org.koin.android.viewmodel.ext.android.viewModel


class TodoFragment : Fragment(), TodoAdapter.TodoListener {

    private lateinit var binding: FragmentTodoBinding

    private val adapter by lazy { TodoAdapter(requireContext(), this) }
//    private val service by lazy { TodoClients.service }
//    private val remoteRepo: TodoRepository by lazy { TodoRepositoryImpl(service) }
//    private val viewModelFactory by lazy { TodoModelFactory(remoteRepo) }
    private val viewModel by viewModel<TodoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false).apply {

            recyclerView.adapter = adapter
            btnTambah.setOnClickListener {
                if (tieTambah.text.toString().isNotEmpty()) {
                    viewModel.insertTodo(
                        TodoModel(
                            title = tieTambah.text.toString()
                        )
                    )
                }

            }
            viewModel.state.observe(viewLifecycleOwner) {
                when (it) {
                    is StatesTodo.Loading -> showLoading(true)
                    is StatesTodo.error -> {
                        showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            it.exception.message ?: "Oops Somethink Wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is StatesTodo.SucceseGetTodo -> {
                        showLoading(false)
                        adapter.list = it.list.toMutableList()

                    }
                    is StatesTodo.SuccessInsert -> {
                        showLoading(false)
                        adapter.insertTodo(it.model)
                        Toast.makeText(
                            requireContext(),
                            "id = ${it.model.id} , berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                        tieTambah.setText("")
                    }
                    is StatesTodo.SuccessUpdateTodo -> {
                        showLoading(false)
                        adapter.updateTodo(it.todo)
                    }
//                    is StatesTodo.SuccessDeleteTodo -> {
//                        showLoading(false)
//                        adapter.deleteTodo(it.todo)
//                    }
                    is StatesTodo.SuccessDeleteTodo -> {
                        showLoading(false)
                        adapter.deleteTodo(it.id)
                        showMesage("${it.id} berhasil dihapus")
                    }
                    else -> throw Exception("Unsupported  state type")
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllTodo()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onChange(todoModel: TodoModel) {
        todoModel.completed = !todoModel.completed
        viewModel.updateTodo(todoModel)
    }

    private fun showMesage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    override fun onDelete(todoModel: TodoModel) {
        viewModel.deleteTodo(todoModel)
    }


}