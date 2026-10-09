/**
 * PoojaMart AI Toy Advisor - Interactive Chat Assistant Widget
 */
(function() {
    document.addEventListener('DOMContentLoaded', initAiAssistant);

    function initAiAssistant() {
        if (document.getElementById('ai-toy-advisor-root')) return;

        const root = document.createElement('div');
        root.id = 'ai-toy-advisor-root';
        root.innerHTML = `
            <!-- Floating Launcher -->
            <button class="ai-chat-launcher" id="ai-launcher-btn" aria-label="Ask PoojaMart AI Toy Advisor">
                <span class="ai-launcher-icon">🤖</span>
                <span>Ask AI Advisor</span>
                <span class="ai-launcher-badge">NEW</span>
            </button>

            <!-- Chat Window -->
            <div class="ai-chat-window" id="ai-chat-window">
                <div class="ai-chat-header">
                    <div class="ai-chat-header-info">
                        <div class="ai-chat-avatar">🧸</div>
                        <div>
                            <div class="ai-chat-header-title">PoojaMart AI Advisor</div>
                            <div class="ai-chat-header-status"><span>●</span> Online & Ready to Help</div>
                        </div>
                    </div>
                    <button class="ai-chat-close-btn" id="ai-close-btn">&times;</button>
                </div>

                <div class="ai-chat-messages" id="ai-messages-container">
                    <!-- Welcome Message -->
                    <div class="ai-msg ai-msg-bot">
                        <div class="ai-bubble">
                            <p>👋 <strong>Vanakkam & Welcome to PoojaMart Toys!</strong></p>
                            <p>I am your smart <strong>AI Toy Advisor</strong>. Looking for the perfect toy for a birthday or special milestone? Ask me anything about toys, age recommendations, or budgets!</p>
                            <div class="ai-chips-container" id="ai-initial-chips">
                                <span class="ai-chip" onclick="handleChipClick('Toys for 1-3 years old baby')">👶 1-3 Yrs Baby Toys</span>
                                <span class="ai-chip" onclick="handleChipClick('Best remote control cars under ₹1500')">🏎️ Top RC Cars</span>
                                <span class="ai-chip" onclick="handleChipClick('Educational STEM puzzles and science sets')">🔬 STEM & Science</span>
                                <span class="ai-chip" onclick="handleChipClick('Cuddly soft teddy bears under ₹500')">🧸 Soft Toys &lt;₹500</span>
                                <span class="ai-chip" onclick="handleChipClick('Action figures and building blocks')">🦸 Action Figures & Blocks</span>
                            </div>
                        </div>
                    </div>
                </div>

                <form class="ai-chat-input-bar" id="ai-chat-form">
                    <input type="text" id="ai-input-field" class="ai-chat-input" placeholder="Ask AI: e.g. Toy for 5 year old under 1000..." autocomplete="off">
                    <button type="submit" class="ai-chat-send-btn" title="Send message">➔</button>
                </form>
            </div>
        `;

        document.body.appendChild(root);

        // Bind events
        const launcher = document.getElementById('ai-launcher-btn');
        const chatWindow = document.getElementById('ai-chat-window');
        const closeBtn = document.getElementById('ai-close-btn');
        const form = document.getElementById('ai-chat-form');
        const input = document.getElementById('ai-input-field');

        launcher.addEventListener('click', () => {
            chatWindow.classList.toggle('active');
            if (chatWindow.classList.contains('active')) {
                input.focus();
            }
        });

        closeBtn.addEventListener('click', () => {
            chatWindow.classList.remove('active');
        });

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const text = input.value.trim();
            if (!text) return;
            input.value = '';
            await sendUserMessage(text);
        });
    }

    window.handleChipClick = function(chipText) {
        sendUserMessage(chipText);
    };

    async function sendUserMessage(text) {
        const messagesContainer = document.getElementById('ai-messages-container');
        if (!messagesContainer) return;

        // 1. Append User Message
        const userMsgDiv = document.createElement('div');
        userMsgDiv.className = 'ai-msg ai-msg-user';
        userMsgDiv.innerHTML = `<div class="ai-bubble">${escapeHtml(text)}</div>`;
        messagesContainer.appendChild(userMsgDiv);
        scrollToBottom(messagesContainer);

        // 2. Append Loading Bot Bubble
        const botMsgDiv = document.createElement('div');
        botMsgDiv.className = 'ai-msg ai-msg-bot';
        botMsgDiv.innerHTML = `<div class="ai-bubble"><span style="color:#878787;">Thinking & finding the best toys for you... ✨</span></div>`;
        messagesContainer.appendChild(botMsgDiv);
        scrollToBottom(messagesContainer);

        try {
            // Call AI endpoint
            const res = await apiRequest('/ai/chat', 'POST', { message: text });

            // Format Markdown bold/newlines
            let formattedReply = escapeHtml(res.reply || 'Here are my recommendations!')
                .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
                .replace(/\*(.*?)\*/g, '<em>$1</em>')
                .replace(/\n/g, '<br>');

            let html = `<div class="ai-bubble">${formattedReply}`;

            // Add Product Cards if available
            if (res.recommendations && res.recommendations.length > 0) {
                html += `<div class="ai-products-preview">`;
                res.recommendations.forEach(p => {
                    html += `
                        <div class="ai-product-card">
                            <img src="${p.imageUrl}" alt="${escapeHtml(p.name)}" class="ai-prod-thumb" onclick="window.location.href='product-detail.html?id=${p.id}'">
                            <div class="ai-prod-details">
                                <a href="product-detail.html?id=${p.id}" class="ai-prod-name" title="${escapeHtml(p.name)}">${escapeHtml(p.name)}</a>
                                <div class="ai-prod-price-row">
                                    <span class="ai-prod-price">₹${(p.price || 0).toLocaleString('en-IN')}</span>
                                    <span style="color:#ff9f00;">★ ${p.rating || 4.8}</span>
                                </div>
                            </div>
                            <button class="ai-prod-add-btn" onclick="quickAddToCart(${p.id})">+ Cart</button>
                        </div>
                    `;
                });
                html += `</div>`;
            }

            // Add Next Suggested Question Chips
            if (res.suggestedQuestions && res.suggestedQuestions.length > 0) {
                html += `<div class="ai-chips-container" style="margin-top:10px;">`;
                res.suggestedQuestions.forEach(q => {
                    html += `<span class="ai-chip" onclick="handleChipClick('${escapeHtml(q).replace(/'/g, "\\'")}')">${escapeHtml(q)}</span>`;
                });
                html += `</div>`;
            }

            html += `</div>`;
            botMsgDiv.innerHTML = html;
        } catch (err) {
            botMsgDiv.innerHTML = `
                <div class="ai-bubble" style="color:#d32f2f;">
                    <p>Sorry, I encountered a temporary connection issue. You can still explore all our toys in the Catalog!</p>
                    <a href="products.html" style="color:#2874f0;font-weight:600;font-size:12px;">Browse All Toys &rarr;</a>
                </div>
            `;
        }
        scrollToBottom(messagesContainer);
    }

    function scrollToBottom(container) {
        setTimeout(() => {
            container.scrollTop = container.scrollHeight;
        }, 50);
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
    }
})();
