AJS.toInit(function ($) {
    var dataLoaded = false; // only load inline dialog contents once
    var dialogId = "confluence-confidentiality-dialog";
    var dialog;
    var $webItem = $('#confluence-confidentiality');
    var url = AJS.contextPath() + "/rest/confluence-confidentiality/1.0/confluence-confidentiality?pageId=" + AJS.Meta.get("page-id");
    var currentConfidentiality;

    var capitalize = function(str) {
        return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
    };

    var updateLabelAndIcon = function(response) {
        currentConfidentiality = response.confidentiality;

        var iconNode = $webItem.children('img');
        var textNode = $webItem.children('span');

        var pictogram;
        switch (response.confidentiality) {
            case 'public':
                pictogram = 'eye';
                break;
            case 'internal':
                pictogram = 'eye_hidden';
                break;
            case 'confidential':
                pictogram = 'lock';
                break;
            default:
                pictogram = 'clipboard';
        }

        var picture = pictogram + '_blue_solid_16x16.png';
        iconNode.attr('src',
            AJS.contextPath()
            + '/download/resources/ch.nine.confluence-confidentiality:confluence-confidentiality-resources/images/'
            + picture);
        textNode.text(capitalize(response.confidentiality))
    };

    var loadConfidentiality = function() {
        $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json'
        }).done(updateLabelAndIcon);
    };

    var registerDialogOpenEvent = function() {
        var saveFn = function(theForm, dialogContent) {
            return function() {
                $.ajax({
                    url: url,
                    type: "POST",
                    dataType: "json",
                    data: $.param(theForm.find('input[checked], input[type!=radio]'))
                }).fail(function() {
                    dataLoaded = false;
                    dialogContent.html(Confluence.Templates.Plugins.ConfluenceConfidentiality.changeError());
                }).done(function(response) {
                    closeDialog();
                    updateLabelAndIcon(response);
                });

                // Show spinner
                dialogContent.html(Confluence.Templates.Plugins.ConfluenceConfidentiality.loading());
                dialogContent.find(".spinner").spin("medium");

                return false;
            }
        };
        dialog = AJS.InlineDialog($webItem, dialogId,
            function (content, trigger, showPopup) {
                if (!dataLoaded) {
                    content.html(Confluence.Templates.Plugins.ConfluenceConfidentiality.loading());
                    content.find(".spinner").spin("medium");
                    $.ajax({
                        url: url,
                        type: "GET",
                        dataType: "json",
                    }).fail(function() {
                        content.html(Confluence.Templates.Plugins.ConfluenceConfidentiality.loadError());
                    }).done(function (response) {
                        var possibleConfidentialitiesNames = [];
                        var possibleConfidentialities = response.possibleConfidentialities;
                        var possibleConfidentialitiesLength = possibleConfidentialities.length;
                        for (var i=0;i<possibleConfidentialitiesLength;i++) {
                            possibleConfidentialitiesNames[possibleConfidentialities[i]] =
                                capitalize(possibleConfidentialities[i]);
                        }
                        response.possibleConfidentialitiesNames = possibleConfidentialitiesNames;
                        response.pageId = AJS.Meta.get("page-id");
                        response.atlToken = AJS.Meta.get("atl-token");
                        content.html(Confluence.Templates.Plugins.ConfluenceConfidentiality.show(response));
                        var theForm = content.find("#confluence-confidentiality-form");
                        content.find("#confluence-confidentiality-submit").click(saveFn(theForm, content));
                        dataLoaded = true;
                    });
                } else {
                    content.find("#confluence-confidentiality-radio-" + currentConfidentiality).prop('checked',true);
                }
                showPopup();
                return false;
            }
        );
    };
    var closeDialog = function() {
        if($('#inline-dialog-' + dialogId).is(':visible')) {
            dialog.hide();
        }
    };
    var registerDialogCloseEvent = function() {
        $webItem.click(closeDialog);
    };

    loadConfidentiality();
    registerDialogOpenEvent();
    registerDialogCloseEvent();
});
