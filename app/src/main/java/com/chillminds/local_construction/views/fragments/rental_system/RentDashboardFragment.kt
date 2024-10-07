package com.chillminds.local_construction.views.fragments.rental_system

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.common.Constants
import com.chillminds.local_construction.databinding.FragmentRentalDashboardBinding
import com.chillminds.local_construction.repositories.remote.ApiCallStatus
import com.chillminds.local_construction.repositories.remote.dto.RentalInformation
import com.chillminds.local_construction.repositories.remote.dto.RentalProduct
import com.chillminds.local_construction.utils.countDaysBetween
import com.chillminds.local_construction.utils.format
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.utils.toDate
import com.chillminds.local_construction.utils.toDateBelowOreo
import com.chillminds.local_construction.view_models.DashboardViewModel
import com.chillminds.local_construction.views.adapters.RentDashBoardTabAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.calendar.CalendarMode
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.maxkeppeler.sheets.info.InfoSheet
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.maxkeppeler.sheets.input.type.spinner.InputSpinner
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar

class RentDashboardFragment : Fragment() {

    lateinit var binding: FragmentRentalDashboardBinding
    val viewModel by inject<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRentalDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabBar()
        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank()) {
                when (it) {
                    Actions.SHOW_RENTAL_REPORT_CREATION_PAGE -> showRentalReportCreationSheet()
                    Actions.SHOW_RENTAL_PRODUCT_CREATION_PAGE -> showRentalProductCreationSheet()
                    Actions.SHOW_RENTAL_DATA_EDIT_DIALOG -> showRentalDataUpdateSheet()
                    Actions.SHOW_RENTAL_INFORMATION_EDIT_DIALOG -> showRentalInformationUpdateSheet()
                    Actions.SHOW_RENT_INFORMATION_DIALOG -> showRentalInformationSheet()
                    Actions.SHOW_RENTAL_PRODUCT_DELETION_CONFIRMATION_DIALOG -> showRentalProductDeleteConfirmation()
                    Actions.SHOW_RENTAL_INFORMATION_DELETION_CONFIRMATION_DIALOG -> showRentalInformationDeleteConfirmation()
                }
            }
        }
    }


    private fun showRentalProductDeleteConfirmation() {
        viewModel.commonModel.rentalProductToDelete.value?.let { projectDetail ->
            InfoSheet().show(requireActivity()) {
                title("Are you sure?")
                this.content("Do you really want to delete ${projectDetail.name}? It you want to continue deletion, press ok.")
                onNegative { viewModel.commonModel.showSnackBar("Rental Product Deletion Cancelled") }
                onPositive {
                    deleteRentalProduct(projectDetail)
                }
            }
        }
    }

    private fun showRentalInformationDeleteConfirmation() {
        viewModel.commonModel.rentalInfoToDelete.value?.let { projectDetail ->
            InfoSheet().show(requireActivity()) {
                title("Are you sure?")
                this.content("Do you really want to delete? It you want to continue deletion, press ok.")
                onNegative { viewModel.commonModel.showSnackBar("Rental Product Deletion Cancelled") }
                onPositive {
                    deleteRentalEntry(projectDetail)
                }
            }
        }
    }


    private fun setTabBar() {
        val tabAdapter = RentDashBoardTabAdapter(this)

        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = Constants.rentDashboardDashList[position]
        }.attach()
    }


    private fun showRentalReportCreationSheet() {
        val productNames = viewModel.commonModel.rentalProductList.value?.map {
            it.name
        } ?: arrayListOf()
        InputSheet().show(requireActivity()) {
            title("Add Rental Entry")
            with(InputSpinner {
                required()
                this.options(listOf("Select Product") + productNames)
                label("Product Name")
            })
            with(InputEditText("customerName") {
                required()
                label("Customer Name")
                hint("Name of the Customer")
            })
            with(InputEditText("phoneNumber") {
                label("Contact Number")
                required()
                inputType(InputType.TYPE_CLASS_NUMBER)
                hint("Phone Number")
            })
            with(InputEditText("place") {
                label("Place")
                hint("Location")
            })
            with(InputEditText("advanceAmount") {
                label("Advance Amount")
                required()
                inputType(InputType.TYPE_CLASS_NUMBER)
                hint("Initial Payment")
            })
            with(InputEditText("productCount") {
                label("Product Count")
                required()
                inputType(InputType.TYPE_CLASS_NUMBER)
                hint("Number of product to rent")
            })
            onNegative { viewModel.commonModel.showSnackBar("Process Cancelled") }
            onPositive { result ->

                val index = result.getInt("0")
                if (index == 0) {
                    viewModel.commonModel.showSnackBar("Select a product")
                    return@onPositive
                }
                val selectedProductToRent =
                    viewModel.commonModel.rentalProductList.value?.firstOrNull { it.name == productNames[index - 1] }

                if (selectedProductToRent == null) {
                    viewModel.commonModel.showSnackBar("Select a valid product")
                    return@onPositive
                }

                val customerName = result.getString("customerName")
                if (customerName.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Name is mandatory")
                    return@onPositive
                }
                val phoneNumber = result.getString("phoneNumber")
                if (phoneNumber.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("phoneNumber is mandatory")
                    return@onPositive
                }
                val place = result.getString("place")
                if (place.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("place is mandatory")
                    return@onPositive
                }
                val advanceAmount = result.getString("advanceAmount")
                if (advanceAmount.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Advance Amount is mandatory")
                    return@onPositive
                }
                val productCount = result.getString("productCount")
                if (productCount.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Count is mandatory")
                    return@onPositive
                }
                viewModel.rentalInformation = RentalInformation(
                    productName = selectedProductToRent.name,
                    productId = selectedProductToRent.id,
                    customerName = customerName ?: "",
                    phoneNumber = phoneNumber ?: "",
                    place = place ?: "",
                    advanceAmount = advanceAmount?.toIntOrNull() ?: 0,
                    productCount = productCount?.toIntOrNull() ?: 0,
                )

                showRentalDateSheet()
            }
        }
    }

    private fun showRentalDateSheet() {
        CalendarSheet().show(requireActivity()) {
            title("Select Rental Date")
            this.calendarMode(CalendarMode.MONTH)
            this.selectionMode(SelectionMode.DATE)
            this.setSelectedDate(Calendar.getInstance())

            onPositive { dateStart, _ ->
                viewModel.rentalInformation?.rentedDate = dateStart.time.format()
                createRentalEntry()
            }
        }
    }

    private fun showRentalReturnDateSheet() {
        CalendarSheet().show(requireActivity()) {
            title("Select Return Date")
            this.calendarMode(CalendarMode.MONTH)
            this.selectionMode(SelectionMode.DATE)
            this.setSelectedDate(Calendar.getInstance())

            onPositive { dateStart, _ ->
                viewModel.rentalInformation?.returnDate = dateStart.time.format()
                updateRentalEntry()
            }
        }
    }

    private fun showRentalProductCreationSheet() {
        InputSheet().show(requireActivity()) {
            title("Create new product")
            with(InputEditText {
                required()
                label("Product Name ")
                hint("Name of the product.")
            })
            with(InputEditText {
                required()
                label("Quantity")
                inputType(InputType.TYPE_CLASS_NUMBER)
                hint("Stock Quantity")
            })
            with(InputEditText {
                label("Rental Price")
                inputType(InputType.TYPE_CLASS_NUMBER)
                hint("Rental Price per day")
            })
            onNegative { viewModel.commonModel.showSnackBar("Product Creation Cancelled") }
            onPositive { result ->
                val name = result.getString("0")
                val quantity = result.getString("1") ?: "0"
                val rentalPrice = result.getString("2") ?: "0"
                if (name.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Product Name is mandatory")
                    return@onPositive
                }

                if (quantity.isNullOrEmptyOrBlank() || quantity.toIntOrNull()
                        ?.greaterThanZero() == false
                ) {
                    viewModel.commonModel.showSnackBar("Quality should be greater than 0")
                    return@onPositive
                }

                if (rentalPrice.isNullOrEmptyOrBlank()) {
                    viewModel.commonModel.showSnackBar("Rental Price is mandatory")
                    return@onPositive
                }
                createProduct(name, quantity.toIntOrNull() ?: 0, rentalPrice.toIntOrNull() ?: 0)
            }
        }
    }

    private fun showRentalDataUpdateSheet() {
        viewModel.commonModel.selectedRentalProduct.value?.let { rentalProduct ->

            InputSheet().show(requireActivity()) {
                title("Update Product")
                with(InputEditText {
                    required()
                    label("Product Name ")
                    hint("Name of the product.")
                    defaultValue(rentalProduct.name)
                })
                with(InputEditText {
                    required()
                    label("Quantity")
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    hint("Stock Quantity")
                    defaultValue(rentalProduct.quantity.toString())
                })
                with(InputEditText {
                    label("Rental Price")
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    hint("Rental Price per day")
                    defaultValue(rentalProduct.rentalPrice.toString())
                })
                onNegative { viewModel.commonModel.showSnackBar("Product Creation Cancelled") }
                onPositive { result ->
                    val name = result.getString("0")
                    val quantity = result.getString("1") ?: "0"
                    val rentalPrice = result.getString("2") ?: "0"
                    if (name.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("Product Name is mandatory")
                        return@onPositive
                    }

                    if (quantity.isNullOrEmptyOrBlank() || quantity.toIntOrNull()
                            ?.greaterThanZero() == false
                    ) {
                        viewModel.commonModel.showSnackBar("Quality should be greater than 0")
                        return@onPositive
                    }

                    if (rentalPrice.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("Rental Price is mandatory")
                        return@onPositive
                    }
                    updateRentalProduct(
                        RentalProduct(
                            rentalProduct.id,
                            name ?: "",
                            quantity.toIntOrNull() ?: 0,
                            rentalPrice.toIntOrNull() ?: 0
                        )
                    )
                }
            }
        }
    }

    private fun showRentalInformationSheet() {
        viewModel.commonModel.selectedRentalInformation.value?.let { rentalProduct ->
            val options = arrayListOf(
                Option("Product Name - ${rentalProduct.productName}"),
                Option("Rent Date - ${rentalProduct.rentedDate}"),
                Option("Count - ${rentalProduct.productCount}"),
                Option("Advance Amount - Rs. ${rentalProduct.advanceAmount}"),
                Option("Customer Name - ${rentalProduct.customerName}"),
                Option("Location - ${rentalProduct.place}"),
                Option("Contact - ${rentalProduct.phoneNumber}"),
                Option("Status " + rentalProduct.productStatus)
            )

            if (rentalProduct.productStatus == viewModel.returnStatus) {
                options.add(Option("Return Date - ${rentalProduct.returnDate}"))
                options.add(Option("Final Payment - ${rentalProduct.finalPayment}"))
            } else {
                val numberOfDays = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    rentalProduct.rentedDate.toDate("dd-MM-yyyy")
                        .countDaysBetween(LocalDateTime.now())
                } else {
                    rentalProduct.rentedDate.toDateBelowOreo("dd-MM-yyyy").countDaysBetween(
                        Calendar.getInstance().time
                    )
                }
                val rentalPrice =
                    viewModel.commonModel.rentalProductList.value?.firstOrNull { it.id == rentalProduct.id }?.rentalPrice
                        ?: 0
                val balance = ((numberOfDays * rentalPrice) - rentalProduct.advanceAmount)
                options.add(Option("Amount to be paid : $balance"))

                options.add(Option("Amount to be paid - ${rentalProduct.returnDate}"))
            }

            if (rentalProduct.productStatus == viewModel.returnStatus)
                OptionSheet().show(requireActivity()) {
                    title("Rental Information")
                    with(options)
                    onNegative { }
                }
        }
    }

    private fun showRentalInformationUpdateSheet() {
        viewModel.commonModel.selectedRentalInformation.value?.let { details ->

            val productNames = viewModel.commonModel.rentalProductList.value?.map {
                it.name
            } ?: arrayListOf()
            val dataToDisplay = listOf("Select Product") + productNames
            val returnStatus = arrayListOf(details.productStatus, viewModel.returnStatus)
            InputSheet().show(requireActivity()) {
                title("Add Rental Entry")
                with(InputSpinner {
                    required()
                    this.options(dataToDisplay)
                    label("Product Name")
                    this.selected(dataToDisplay.indexOf(details.productName))
                })
                with(InputEditText("customerName") {
                    required()
                    label("Customer Name")
                    hint("Name of the Customer")
                    defaultValue(details.customerName)
                })
                with(InputEditText("phoneNumber") {
                    label("Contact Number")
                    required()
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    hint("Phone Number")
                    defaultValue(details.phoneNumber)
                })
                with(InputEditText("place") {
                    label("Place")
                    hint("Location")
                    defaultValue(details.place)
                })
                with(InputEditText("advanceAmount") {
                    label("Advance Amount")
                    required()
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    hint("Initial Payment")
                    defaultValue(details.advanceAmount)
                })
                with(InputEditText("productCount") {
                    label("Product Count")
                    required()
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    hint("Number of product to rent")
                    defaultValue(details.productCount)
                })
                with(InputEditText("returnProductCount") {
                    label("Return Count")
                    required()
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    defaultValue(details.returnProductCount ?: 0)
                })
                with(InputSpinner("status") {
                    required()
                    this.options(returnStatus)
                    label("Status")
                    this.selected(dataToDisplay.indexOf(details.productStatus))
                })
                with(InputEditText("finalPayment") {
                    label("Payment made")
                    required()
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    hint("Payment made")
                    defaultValue((details.finalPayment ?: 0).toString())
                })
                onNegative { viewModel.commonModel.showSnackBar("Process Cancelled") }
                onPositive { result ->

                    val index = result.getInt("0")
                    if (index == 0) {
                        viewModel.commonModel.showSnackBar("Select a product")
                        return@onPositive
                    }
                    val selectedProductToRent =
                        viewModel.commonModel.rentalProductList.value?.firstOrNull { it.name == productNames[index - 1] }

                    if (selectedProductToRent == null) {
                        viewModel.commonModel.showSnackBar("Select a valid product")
                        return@onPositive
                    }

                    val customerName = result.getString("customerName")
                    if (customerName.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("Name is mandatory")
                        return@onPositive
                    }
                    val phoneNumber = result.getString("phoneNumber")
                    if (phoneNumber.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("phoneNumber is mandatory")
                        return@onPositive
                    }
                    val place = result.getString("place")
                    if (place.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("place is mandatory")
                        return@onPositive
                    }
                    val advanceAmount = result.getString("advanceAmount")
                    if (advanceAmount.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("Advance Amount is mandatory")
                        return@onPositive
                    }
                    val productCount = result.getString("productCount")
                    if (productCount.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("Count is mandatory")
                        return@onPositive
                    }
                    val returnProductCount = result.getString("returnProductCount")
                    if (returnProductCount.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("Return Count is mandatory")
                        return@onPositive
                    }
                    val status = result.getInt("status")

                    val selectedStatus = returnStatus[status]

                    val finalPayment = result.getString("finalPayment") ?: "0"
                    if (finalPayment.isNullOrEmptyOrBlank()) {
                        viewModel.commonModel.showSnackBar("Payment is mandatory")
                        return@onPositive
                    }

                    viewModel.rentalInformation = RentalInformation(
                        id = details.id,
                        productName = selectedProductToRent.name,
                        productId = selectedProductToRent.id,
                        productStatus = selectedStatus,
                        customerName = customerName ?: "",
                        phoneNumber = phoneNumber ?: "",
                        place = place ?: "",
                        finalPayment = finalPayment.toIntOrNull() ?: 0,
                        advanceAmount = advanceAmount?.toIntOrNull() ?: 0,
                        returnProductCount = returnProductCount?.toIntOrNull() ?: 0,
                        productCount = productCount?.toIntOrNull() ?: 0,
                    )
                    if (selectedStatus != viewModel.returnStatus) {
                        updateRentalEntry()
                    } else {
                        showRentalReturnDateSheet()
                    }
                }
            }
        }
    }

    private fun createProduct(name: String?, quantity: Int, rentalPrice: Int) {
        viewModel.createRentalProduct(name ?: "", quantity, rentalPrice)
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    ApiCallStatus.LOADING -> {
                        viewModel.commonModel.showProgress()
                    }

                    ApiCallStatus.ERROR -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Failed to create a product.")
                    }

                    ApiCallStatus.SUCCESS -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Product Created Successfully.")
                        viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PRODUCT_LIST)
                    }
                }
            }
    }

    private fun createRentalEntry() {
        viewModel.createRentalEntry()
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    ApiCallStatus.LOADING -> {
                        viewModel.commonModel.showProgress()
                    }

                    ApiCallStatus.ERROR -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Failed to add rental information.")
                    }

                    ApiCallStatus.SUCCESS -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Rental Entry Created Successfully.")
                        viewModel.commonModel.actionListener.postValue(Actions.REFRESH_RENTAL_PRODUCT_LIST)
                    }
                }
            }
    }

    private fun updateRentalEntry() {
        viewModel.updateRentalEntry()
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    ApiCallStatus.LOADING -> {
                        viewModel.commonModel.showProgress()
                    }

                    ApiCallStatus.ERROR -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Failed to update rental information.")
                    }

                    ApiCallStatus.SUCCESS -> {
                        viewModel.commonModel.cancelProgress()
                        viewModel.commonModel.showSnackBar("Rental Entry updated Successfully.")
                        viewModel.commonModel.actionListener.postValue(Actions.REFRESH_RENTAL_PRODUCT_LIST)
                    }
                }
            }
    }

    private fun updateRentalProduct(data: RentalProduct) {
        viewModel.updateRentalProduct(data).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.showProgress()
                }

                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Failed to update.")
                }

                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Updated Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PRODUCT_LIST)
                }
            }
        }
    }

    private fun deleteRentalProduct(data: RentalProduct) {
        viewModel.deleteRentalProduct(data).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.showProgress()
                }

                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Failed to delete a product.")
                }

                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Product Deleted Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_PRODUCT_LIST)
                }
            }
        }
    }

    private fun deleteRentalEntry(data: RentalInformation) {
        viewModel.deleteRentalEntry(data).observe(viewLifecycleOwner) {
            when (it.status) {
                ApiCallStatus.LOADING -> {
                    viewModel.commonModel.showProgress()
                }

                ApiCallStatus.ERROR -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Failed to delete a Entry.")
                }

                ApiCallStatus.SUCCESS -> {
                    viewModel.commonModel.cancelProgress()
                    viewModel.commonModel.showSnackBar("Entry Deleted Successfully.")
                    viewModel.commonModel.actionListener.postValue(Actions.REFRESH_RENTAL_PRODUCT_LIST)
                }
            }
        }
    }

}

private fun Int?.greaterThanZero(): Boolean = (this ?: 0) > 0

