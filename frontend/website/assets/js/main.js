/* ============================================================
   MusicLinkd — musiclinkd.com
   Interactions: GSAP ScrollTrigger, parallax, counters,
   horizontal gallery, FAQ, forms, device animations
   ============================================================ */
(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", init);
  let mm;

  function init() {
    initNav();
    initFAQ();
    initWaitlist();
    initPartnerForm();
    initCounters();
    initGallery();
    initReveal();
    initJourneyTabs();
    initDeviceAnim();
    initParallax();
  }

  /* ---------- Sticky nav ---------- */
  function initNav() {
    const nav = document.querySelector(".nav");
    if (!nav) return;
    const onScroll = () => nav.classList.toggle("nav--scrolled", window.scrollY > 20);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });

    const toggle = document.getElementById("navToggle");
    const links = document.getElementById("navLinks");
    if (!toggle || !links) return;
    toggle.addEventListener("click", () => {
      const open = links.classList.toggle("open");
      toggle.setAttribute("aria-expanded", String(open));
    });
    links.querySelectorAll("a").forEach((a) => a.addEventListener("click", () => links.classList.remove("open")));
  }

  /* ---------- FAQ accordion ---------- */
  function initFAQ() {
    document.querySelectorAll(".faq__q").forEach((btn) => {
      btn.addEventListener("click", () => {
        const expanded = btn.getAttribute("aria-expanded") === "true";
        btn.setAttribute("aria-expanded", String(!expanded));
      });
    });
  }

  /* ---------- Journey tabs ---------- */
  function initJourneyTabs() {
    document.querySelectorAll(".journey-tab").forEach((tab) => {
      tab.addEventListener("click", () => {
        const target = tab.dataset.tab;
        document.querySelectorAll(".journey-tab").forEach((t) => t.setAttribute("aria-selected", String(t === tab)));
        document.querySelectorAll(".journey-panel").forEach((p) => {
          if (p.id === target) p.removeAttribute("hidden");
          else p.setAttribute("hidden", "");
        });
      });
    });
  }

  /* ---------- Waitlist ---------- */
  function initWaitlist() {
    document.querySelectorAll(".waitlist").forEach((form) => {
      form.addEventListener("submit", (e) => {
        e.preventDefault();
        const input = form.querySelector("input[type='email']");
        const email = input.value.trim();
        if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
          input.style.outline = "2px solid var(--error)";
          setTimeout(() => (input.style.outline = ""), 1800);
          return;
        }
        form.classList.add("waitlist--done");
        console.log("Captured:", email, "->", form.dataset.endpoint || "no endpoint configured");
      });
    });
  }

  /* ---------- Partner / Business Demo form ---------- */
  function initPartnerForm() {
    document.querySelectorAll(".partner-form").forEach((form) => {
      form.addEventListener("submit", (e) => {
        e.preventDefault();
        const name = form.querySelector('input[name="name"]')?.value.trim();
        const company = form.querySelector('input[name="company"]')?.value.trim();
        const email = form.querySelector('input[name="email"]')?.value.trim();
        if (!name || !company || !email) {
          form.querySelectorAll("input").forEach((el) => {
            el.style.outline = "2px solid var(--error)";
            setTimeout(() => (el.style.outline = ""), 1800);
          });
          return;
        }
        form.classList.add("partner-form--done");
        console.log("Partner enquiry:", { name, company, email }, "->", form.dataset.endpoint || "no endpoint");
      });
    });
  }

  /* ---------- Number counters (GSAP) ---------- */
  function initCounters() {
    if (typeof gsap === "undefined" || typeof ScrollTrigger === "undefined") return;
    gsap.registerPlugin(ScrollTrigger);

    document.querySelectorAll("[data-count]").forEach((el) => {
      const target = Number(el.dataset.count);
      const suffix = el.dataset.suffix || "";
      const prefix = el.dataset.prefix || "";
      ScrollTrigger.create({
        trigger: el,
        start: "top 85%",
        once: true,
        onEnter: () => {
          gsap.to({ val: 0 }, {
            val: target,
            duration: 1.6,
            ease: "power3.out",
            onUpdate: function () {
              el.textContent = prefix + Math.round(this.targets()[0].val).toLocaleString("en-IN") + suffix;
            },
          });
        },
      });
    });
  }

  /* ---------- Horizontal gallery with pinned scrolling ---------- */
  function initGallery() {
    const gallery = document.querySelector(".gallery");
    if (!gallery || typeof gsap === "undefined" || typeof ScrollTrigger === "undefined") return;
    gsap.registerPlugin(ScrollTrigger);

    const viewport = gallery.querySelector(".gallery__viewport");
    const track = gallery.querySelector(".gallery__track");
    const progress = gallery.querySelector(".gallery__progress i");
    if (!viewport || !track) return;

    const scrollWidth = () => track.scrollWidth - viewport.clientWidth;

    if (window.innerWidth <= 900) {
      viewport.style.overflowX = "auto";
      viewport.style.scrollSnapType = "x mandatory";
      viewport.style.webkitOverflowScrolling = "touch";
      viewport.style.scrollbarWidth = "none";
      return;
    }

    viewport.style.overflow = "hidden";

    ScrollTrigger.create({
      trigger: gallery,
      start: "top 10%",
      end: () => "+=" + scrollWidth(),
      pin: true,
      scrub: 1.2,
      anticipatePin: 1,
      invalidateOnRefresh: true,
      onUpdate: (self) => {
        gsap.set(track, { x: -self.progress * scrollWidth() });
        if (progress) progress.style.width = (self.progress * 100) + "%";
      },
    });
  }

  /* ---------- Section reveals with staggered children ---------- */
  function initReveal() {
    if (typeof gsap === "undefined" || typeof ScrollTrigger === "undefined") return;
    gsap.registerPlugin(ScrollTrigger);

    document.querySelectorAll(".reveal").forEach((el, i) => {
      gsap.from(el, {
        y: 36,
        opacity: 0,
        duration: 0.7,
        delay: i * 0.04,
        ease: "power2.out",
        scrollTrigger: { trigger: el, start: "top 88%", once: true },
      });
    });

    // Staggered child items
    document.querySelectorAll("[data-stagger]").forEach((parent) => {
      const items = parent.children;
      if (!items.length) return;
      gsap.from(items, {
        y: 24,
        opacity: 0,
        duration: 0.5,
        stagger: 0.05,
        ease: "power2.out",
        scrollTrigger: { trigger: parent, start: "top 85%", once: true },
      });
    });
  }

  /* ---------- Device mockup entrance animation ---------- */
  function initDeviceAnim() {
    if (typeof gsap === "undefined" || typeof ScrollTrigger === "undefined") return;

    // Hero phones float
    const phoneA = document.querySelector(".hero__phone-a");
    const phoneB = document.querySelector(".hero__phone-b");
    if (phoneA) {
      gsap.from(phoneA, { y: 40, opacity: 0, duration: 0.8, delay: 0.2, ease: "power2.out" });
    }
    if (phoneB) {
      gsap.from(phoneB, { y: 60, opacity: 0, duration: 0.8, delay: 0.4, ease: "power2.out" });
    }

    // Device stack hover (CSS handles hover, GSAP handles entrance)
    document.querySelectorAll(".device-stack").forEach((stack) => {
      gsap.from(stack.children, {
        y: 60,
        opacity: 0,
        duration: 0.7,
        stagger: 0.1,
        ease: "power3.out",
        scrollTrigger: { trigger: stack, start: "top 80%", once: true },
      });
    });
  }

  /* ---------- Subtle parallax on images ---------- */
  function initParallax() {
    if (typeof gsap === "undefined" || typeof ScrollTrigger === "undefined") return;

    document.querySelectorAll("[data-parallax]").forEach((el) => {
      const rate = Number(el.dataset.parallax) || 0.15;
      gsap.fromTo(el, { y: -rate * 100 }, {
        y: rate * 100,
        ease: "none",
        scrollTrigger: { trigger: el.parentElement || el, start: "top bottom", end: "bottom top", scrub: 1 },
      });
    });
  }
})();