$(document).ready(function () {

    $("#search-form").submit(function (event) {
        event.preventDefault();
        search_submit();
    });


    $("#term").autocomplete({
        minChars: 2,
        noCache: true,
        serviceUrl: '/users/api/partial',
        paramName: 'partialTerm',
        dataType: 'json',
        onSelect: suggest
    });
});


function suggest(suggestion) {
    search_submit()
}


function search_submit() {
	var t0 = performance.now();
    var search = {};
    search["term"] = $("#term").val();

    $("#btn-search").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/users/api/search",
        data: JSON.stringify(search),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            $('#feedback').empty();
            var table = [];
            if (data.msg === 'found') {
            	var total = data.results.total;
            	var t1 = performance.now();
            	var diff = ((t1 - t0)/1000).toFixed(2);
            	$('#feedback').append('Returned ' + total + ' records in ' + diff + ' seconds!');
                var items = data.results.list;
                for (var i = 0; items.length > i; i++) {
                    var item = items[i];
                    table.push([item.photo, item.firstName, item.lastName, item.designation, item.company, item.city, item.country, item.email, item.userName, item.website, item.info])
                }
                makeTable($('#feedback'), table);
                $("#btn-search").prop("disabled", false);
            } else {
                $('#feedback').append('No Records Found');
            }
        },
        error: function (e) {
            $('#feedback').empty();
            var json = "<h4>Search Error Response</h4><pre>"
                + e.responseText + "</pre>";
            $('#feedback').html(json);

            console.log("ERROR : ", e);
            $("#btn-search").prop("disabled", false);

        }
    });

    function makeTable(container, data) {
        var table = $("<table/>").addClass('table table-striped table-dark');
        var headers = ["Profile Photo", "First Name", "Last Name", "Designation", "Company", "City", "Country", "Email", "Username", "Website", "Tag"];
        var thead = $("<thead/>").addClass('thead-dark');
        var heads = $("<tr/>");
        $.each(headers, function (colIndex, c) {
            heads.append($("<th/>").text(c));
        });
        var tbody = $("<tbody/>");
        table.append(thead.append(heads));
        
        $.each(data, function (rowIndex, r) {
            var row = $("<tr/>");
            $.each(r, function (colIndex, c) {
            	if (colIndex == 0) {
            		row.append($("<td/>").html('<img src="'+c+'" />'));
            	} else {
            		row.append($("<td/>").text(c));
            	}
            });
            tbody.append(row);
        });
        table.append(tbody);
        return container.append(table);
    }


}