package com.example.bunkbuddy.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.PopUpToBuilder
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bunkbuddy.R
import com.example.bunkbuddy.UI.SubjectViewModel
import com.example.bunkbuddy.activities.MainActivity
import com.example.bunkbuddy.databinding.FragmentAllSubjectsBinding
import com.example.bunkbuddy.datamodel.Subject
import com.example.bunkbuddy.util.SubjectAdapter
import com.example.bunkbuddy.util.subjectItemClickListener
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Collections
import java.util.Locale

class AllSubjectsFragment : Fragment(), subjectItemClickListener {
    private var _binding: FragmentAllSubjectsBinding? = null
    private val binding get()=_binding!!

    private lateinit var listener: subjectItemClickListener
    private var popUpWindow: PopupWindow? = null
    private lateinit var viewModel: SubjectViewModel
    private lateinit var adapter: SubjectAdapter
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.also { listener = it }
        viewModel = (activity as MainActivity).viewModel

        sharedPreference = (activity as MainActivity).sharedPreferences
        editor = sharedPreference.edit()
        val lastUpdatedDate = sharedPreference.getString("last_updated_date", "")
        val lastUpdatedTime = sharedPreference.getString("last_updated_time", "")
        if(lastUpdatedDate.isNullOrEmpty()) binding.lastUpdatedTv.visibility = View.GONE
        else {
            binding.lastUpdatedTv.visibility = View.VISIBLE
            binding.lastUpdatedTv.text = "Last updated on $lastUpdatedDate at $lastUpdatedTime"
        }

        setUpRecyclerView()
        val dayAndDate = getDayAndDate()
        setCurrentDate(dayAndDate[1])
        viewModel.savedSubjects.observe(viewLifecycleOwner, Observer {
            it?.let{list->
                adapter.setData(list)
            }
        })

        binding.addSubjectIv.root.setOnClickListener {
            showAddSubjectPopup()
        }

        binding.allSubjectsRcv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy>0){
                    hideViews()
                }
                else showViews()
            }
        })
    }

    private fun setCurrentDate(date: String) {
        binding.currentDateTv.text = date
    }

    private fun showUndoSnackbar(deletedItem: Subject, position: Int) {
        val snackbar = Snackbar.make(
            requireView(),
            "Subject deleted",
            Snackbar.LENGTH_LONG
        )

        snackbar.setAction("Undo") {
            undoDelete(deletedItem, position)
        }

        snackbar.show()
    }
    private fun undoDelete(deletedItem: Subject, position: Int) {
        viewModel.addSubject(deletedItem)
        adapter.addItem(deletedItem, position)
    }
    private fun setUpRecyclerView() {
        adapter = SubjectAdapter(requireContext(), listener)
        binding.allSubjectsRcv.adapter = adapter
        binding.allSubjectsRcv.layoutManager=LinearLayoutManager(requireContext())
        val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    source: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val sourcePos = source.adapterPosition
                    val desPos = target.adapterPosition
                    adapter.swap(sourcePos, desPos)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val subject = adapter.getAtPostion(pos)
                    adapter.deleteAt(pos)
                    viewModel.deleteSubject(subject)
                    showUndoSnackbar(subject, pos)

                }

            }
        )
        itemTouchHelper.attachToRecyclerView(binding.allSubjectsRcv)
    }

    private fun showAddSubjectPopup() {
        val popUpView = LayoutInflater.from(requireContext()).inflate(R.layout.add_subject_popup, null)

        val incAttendedBtn: CardView = popUpView.findViewById(R.id.incAttendenceButton)
        val decAttendedBtn: CardView = popUpView.findViewById(R.id.decAttendenceButton)
        val incMissedBtn: CardView = popUpView.findViewById(R.id.incMissedButton)
        val decMissedBtn: CardView = popUpView.findViewById(R.id.decMissedButton)
        val incRequirementBtn: CardView = popUpView.findViewById(R.id.incRequirementButton)
        val decRequirementBtn: CardView = popUpView.findViewById(R.id.decRequirementButton)
        val subjectNameEdtxt: EditText = popUpView.findViewById(R.id.subjectNameEdTxt)

        val attendedTv: TextView = popUpView.findViewById(R.id.attendedNoOfClassesTv)
        val missedClassTv: TextView = popUpView.findViewById(R.id.missedNoOfClassesTv)
        val requirementPercTv: TextView = popUpView.findViewById(R.id.requirementTv)

        val addBtn: Button = popUpView.findViewById(R.id.addSubjectBtn)
        val cancelBtn: Button = popUpView.findViewById(R.id.cancelBtn)

        val attended = MutableLiveData(1)
        val missed = MutableLiveData(0)
        val requirement = MutableLiveData(75)
        val addBtnState = MutableLiveData(false)

        addBtnState.observe(viewLifecycleOwner, Observer {
            addBtn.isEnabled = it
        })

        subjectNameEdtxt.addTextChangedListener {
            if(it.isNullOrEmpty()){
                addBtnState.postValue(false)
            }
            else addBtnState.postValue(attended.value!!+missed.value!!>0)
        }


        attended.observe(viewLifecycleOwner, Observer {
            if(it>0 && !subjectNameEdtxt.text.isNullOrEmpty()) addBtnState.postValue(true)
            attendedTv.text = it.toString()
        })

        missed.observe(viewLifecycleOwner, Observer {
            if(it>0 && !subjectNameEdtxt.text.isNullOrEmpty()) addBtnState.postValue(true)
            missedClassTv.text = it.toString()
        })

        requirement.observe(viewLifecycleOwner, Observer{
            it?.let{
                requirementPercTv.text = it.toPercent(it)
            }
        })

        incAttendedBtn.setOnClickListener {
            attended.postValue(attended.value!!.plus(1))
        }
        decAttendedBtn.setOnClickListener {
            attended.value?.let{
                if(it>0) attended.postValue(it.minus(1))
            }
        }

        incMissedBtn.setOnClickListener {
            missed.value?.let{
                missed.postValue(it.plus(1))
            }
        }
        decMissedBtn.setOnClickListener {
            missed.value?.let{
                if(it>0) missed.postValue(it.minus(1))
            }
        }

        incRequirementBtn.setOnClickListener {
            requirement.value?.let{ req->
                if(req<100) requirement.postValue(req.plus(5))
            }
        }
        decRequirementBtn.setOnClickListener {
            requirement.value?.let{req->
                if(req>0) requirement.postValue(req.minus(5))
            }
        }

        popUpWindow = PopupWindow(
            popUpView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popUpWindow?.isFocusable=true

        addBtn.setOnClickListener {
            addNewSubject(Subject(subjectNameEdtxt.text.toString().trim(), missed.value!!, attended.value!!, 0, getDayAndDate()[1], requirement.value!!))
            popUpView.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up_out_anim))
            popUpWindow?.dismiss()
        }

        cancelBtn.setOnClickListener {
            popUpView.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up_out_anim))
            popUpWindow?.dismiss()
        }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up_in_anim)
        popUpView.startAnimation(animation)
        popUpWindow?.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0)
    }

    private fun hideViews() {
        binding.addSubjectIv.root.animate()
            .translationY(binding.addSubjectIv.root.height.toFloat())
            .withEndAction {
                binding.addSubjectIv.root.visibility = View.GONE
            }
            .setDuration(300)
            .start()
    }

    private fun showViews() {
        binding.addSubjectIv.root.animate()
            .translationY(0f)
            .withEndAction {
                binding.addSubjectIv.root.visibility = View.VISIBLE
            }
            .start()
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun addNewSubject(subject: Subject){
        viewModel.addSubject(subject)
        val dayAndDate = getDayAndDate()
        editor.putString("last_updated_date", dayAndDate[1])
        editor.putString("last_updated_time", dayAndDate[0])
        editor.apply()
        updateDateAndTime(dayAndDate)
    }

    override fun onDeleteBtnClicked(subject: Subject) {
        viewModel.deleteSubject(subject)
        val dayAndDate = getDayAndDate()
        editor.putString("last_updated_date", dayAndDate[1])
        editor.putString("last_updated_time", dayAndDate[0])
        updateDateAndTime(getDayAndDate())
    }

    private fun Int.toPercent(a: Int): String{
        return "${a}%"
    }

    private fun getDayAndDate():List<String> {
        val currentDate = Calendar.getInstance().time

        return listOf(
            SimpleDateFormat("HH:mm", Locale.US).format(currentDate),
            SimpleDateFormat("d MMM yyyy", Locale.US).format(currentDate)
        )
    }
    private fun updateSubject(subject: Subject){
        viewModel.updateSubjectAndLectures(subject)
    }
    private fun updateDateAndTime(list: List<String>){
        binding.lastUpdatedTv.text = "Last updated on ${list[1]} at ${list[0]}"
    }
    override fun onIncreaseAttendenceBtnClicked(subject: Subject) {
        val dayAndDate = getDayAndDate()
        editor.putString("last_updated_date", dayAndDate[1])
        editor.putString("last_updated_time", dayAndDate[0])
        editor.apply()
        updateDateAndTime(dayAndDate)
        subject.lastUpdated = getDayAndDate()[1]
        subject.attended = subject.attended.inc()
        updateSubject(subject)
    }
    override fun onDecreaseAttendenceBtnClicked(subject: Subject) {
        val dayAndDate = getDayAndDate()
        editor.putString("last_updated_date", dayAndDate[1])
        editor.putString("last_updated_time", dayAndDate[0])
        editor.apply()
        updateDateAndTime(dayAndDate)
        subject.attended = subject.attended.dec()
        subject.lastUpdated = getDayAndDate()[1]
        updateSubject(subject)
    }
    override fun onIncreaseMissedBtnClicked(subject: Subject) {
        val dayAndDate = getDayAndDate()
        editor.putString("last_updated_date", dayAndDate[1])
        editor.putString("last_updated_time", dayAndDate[0])
        editor.apply()
        updateDateAndTime(dayAndDate)
        subject.lastUpdated = dayAndDate[1]
        subject.missed = subject.missed.inc()
        updateSubject(subject)
        editor
    }
    override fun onDecreaseMissedBtnClicked(subject: Subject) {
        val dayAndDate = getDayAndDate()
        editor.putString("last_updated_date", dayAndDate[1])
        editor.putString("last_updated_time", dayAndDate[0])
        editor.apply()
        updateDateAndTime(dayAndDate)
        subject.lastUpdated = getDayAndDate()[1]
        subject.missed = subject.missed.dec()
        updateSubject(subject)
    }
}