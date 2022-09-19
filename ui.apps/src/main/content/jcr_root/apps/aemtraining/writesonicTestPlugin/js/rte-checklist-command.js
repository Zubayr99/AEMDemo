(function (RTE, Class) {
    const GROUP = 'checkList';
    const CHECKED_FEATURE = 'checkeditem';
    const UNCHECKED_FEATURE = 'uncheckeditem';
    const WHATEVER = 'Whatever';
    const ITEM_COMMON_CLASS = 'check-item';


    RTE.commands.checkListCmd = new Class({
        extend: RTE.commands.Command,

        toString: 'checkListCmd',

        isCommand: function (cmdStr) {
            return (cmdStr === CHECKED_FEATURE) || (cmdStr === UNCHECKED_FEATURE) || (cmdStr === WHATEVER);
        },

        getProcessingOptions: function () {
            const cmd = RTE.commands.Command;
            return cmd.PO_SELECTION | cmd.PO_NODELIST;
        },

        execute: function (execDef) {

            const text = execDef.nodeList.commonAncestor.textContent;

            fetch("https://api.writesonic.com/v1/business/content/content-rephrase?engine=economy&language=en", {
                "headers": {
                    "content-type": "application/json",
                    "accept": "application/json",
                    "X-API-KEY": "a055b444-1abe-4c8e-afa2-59f892743f0a"
                }, "body": JSON.stringify({"content_to_rephrase": text, "tone_of_voice": "excited"}),
                "method": "POST",
            }).then(res => {
                window.alert(res.status)
                if (res && res.status === 200) {
                    return res.json();
                }
            }).then(function (data) {
                console.log(data)
            });

        }
    });

// register command
    RTE.commands.CommandRegistry.register(GROUP, RTE.commands.checkListCmd);
})(CUI.rte, window.Class);
