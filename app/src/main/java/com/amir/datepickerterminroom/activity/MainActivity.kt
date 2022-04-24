package com.amir.datepickerterminroom.activity

import android.app.ActionBar
import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.text.MessageFormat.format
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.format
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Adapter

import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.datepickerterminroom.AdapterTerm
import com.amir.datepickerterminroom.SwipeToDeleteCallback

import com.amir.datepickerterminroom.databinding.ActivityMainBinding
import com.amir.datepickerterminroom.databinding.CustomDialogAddTermBinding
import com.amir.datepickerterminroom.room.AppTerm
import com.amir.datepickerterminroom.room.DaoTerm
import com.amir.datepickerterminroom.room.EntityTerm
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.String.format
import java.security.spec.PSSParameterSpec.DEFAULT
import java.text.MessageFormat.format
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener


    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //TODO  toolbar
        setSupportActionBar(binding?.toolbarMain)

        //TODO Application
        val termDao = (application as AppTerm).db.daoTerm()


        binding?.flAdd?.setOnClickListener {
            customDialogAdd()
        }

        lifecycleScope.launch {
            termDao.fetchAllTerms().collect {
                val list = List(it)
                setupListOfRecyclerView(list as MutableList<EntityTerm>, termDao)
            }
        }


    }

    private fun List(size: MutableList<EntityTerm>): List<EntityTerm> {
        return size
    }

    //todo setup rv
    private fun setupListOfRecyclerView(termList: MutableList<EntityTerm>, daoTerm: DaoTerm) {
        if (termList.isNotEmpty()) {
            val adapterTerm = AdapterTerm(termList
            ) { updateId ->
                updateRecord(updateId, daoTerm)
            }
            binding?.rvRecords?.layoutManager = LinearLayoutManager(this)
              binding?.rvRecords?.adapter = adapterTerm
            swipeToDelete(daoTerm, termList)


        }
    }

    //todo add a record
    private fun addTerm(daoTerm: DaoTerm, etTerm: AppCompatEditText, etDate: AppCompatEditText) {
        val term = etTerm.text.toString()
        val date = etDate.text.toString()

        if (term.isNotEmpty() && date.isNotEmpty()) {
            lifecycleScope.launch {
                daoTerm.insert(EntityTerm(term = term, date = date))
                Toast.makeText(
                    this@MainActivity, "Record is saved", Toast.LENGTH_LONG
                ).show()
                etTerm.text?.clear()

            }
        } else {
            Toast.makeText(
                this@MainActivity, "The Appointment or Date is empty!", Toast.LENGTH_LONG
            ).show()
        }
    }

    //todo swipe to delete
    private fun swipeToDelete(daoTerm: DaoTerm, entityTermList: MutableList<EntityTerm>) {

        val swipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                lifecycleScope.launch {
                    daoTerm.delete(EntityTerm(entityTermList[viewHolder.adapterPosition].id))
                    Toast.makeText(
                        this@MainActivity,
                        "Delete Success",
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding?.rvRecords)
    }

    //todo update
    private fun updateRecord(id: Int, daoTerm: DaoTerm) {

        val updateDialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        updateDialog.setCancelable(false)
        val binding = CustomDialogAddTermBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)
        binding.btnAdd.text = "Update"

        dateView(binding.etDate)

        binding.etDate.setOnClickListener {
            dateSet(binding.etDate)
        }
        
        lifecycleScope.launch {
            daoTerm.fetchTermById(id).collect {
                binding.etTerm.setText(it.term)
                binding.etDate.setText(it.date)

            }
        }

        binding.btnAdd.setOnClickListener {
            val term = binding.etTerm.text.toString()
            val date = binding.etDate.text.toString()
            if (term.isNotEmpty() && date.isNotEmpty()) {
                lifecycleScope.launch {
                    daoTerm.update(EntityTerm(id, term, date))
                    Toast.makeText(this@MainActivity, "Record updated", Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(this@MainActivity, "Appointment or Date is empty", Toast.LENGTH_LONG)
                    .show()
            }
        }
        binding.btnCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    //todo add
    private fun customDialogAdd() {
        val termDao = (application as AppTerm).db.daoTerm()
        val dialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        dialog.setCancelable(false)
        val binding = CustomDialogAddTermBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        dateView(binding.etDate)

        binding.etDate.setOnClickListener {
            dateSet(binding.etDate)
        }


        binding.btnAdd.setOnClickListener {
            val term = binding.etTerm.text.toString()
            val date = binding.etDate.text.toString()
            addTerm(termDao, binding.etTerm, binding.etDate)


            Log.i("term input et $term => $date", term)
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        /**
         * the custom dialog was to small to change it manuely we need the follow code
         * https://stackoverflow.com/questions/19133822/custom-dialog-too-small
         */
        val metric: DisplayMetrics = resources.displayMetrics
        val width = metric.widthPixels
        dialog.window?.setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT)

        dialog.show()

    }

    //TODO DATE PICKER
    private fun dateSet(etDate: AppCompatEditText) {
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateView(etDate)
        }
        DatePickerDialog(
            this,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * when the user open the dialog with follow function we set the date prepared
     * we need this function as well to pass the date format to datePicker as view
     */
    private fun dateView(etDate: AppCompatEditText) {
        val dFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(dFormat, Locale.getDefault())
        etDate.setText(sdf.format(cal.time).toString())
    }
}